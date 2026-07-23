package com.gymcore.seeder;

import org.springframework.beans.factory.annotation.Value;
import com.github.javafaker.Faker;
import com.gymcore.entity.*;
import com.gymcore.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ZoneRepository zoneRepository;
    private final EquipmentRepository equipmentRepository;
    private final MemberRepository memberRepository;
    private final CheckInRepository checkInRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(new Locale("hr"));
    private final Random random = new Random();

    @Value("${seeder.default-password:password123}")
    private String defaultPassword;

    @Override
    public void run(String... args) {
        if (tenantRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("Starting database seeding...");

        Tenant tenant = seedTenant();
        List<Location> locations = seedLocations(tenant);

        for (Location location : locations) {
            List<com.gymcore.entity.Zone> zones = seedZones(location);
            for (com.gymcore.entity.Zone zone : zones) {
                seedEquipment(zone);
            }
            List<Member> members = seedMembers(location, tenant);
            seedCheckIns(members, location);
        }

        log.info("Database seeding completed!");
    }

    private Tenant seedTenant() {
        Tenant tenant = new Tenant();
        tenant.setName("GymCore");
        tenant.setSubdomain("gymcore");
        tenant.setContactEmail("admin@gymcore.hr");
        tenant.setSubscriptionPlan(SubscriptionPlan.ULTIMATE);
        tenant.setActive(true);
        return tenantRepository.save(tenant);
    }

    private List<Location> seedLocations(Tenant tenant) {
        List<Location> locations = new ArrayList<>();

        // Zagreb lokacije (7)
        String[] zagrebNames = {
                "GymCore Zagreb Centar", "GymCore Zagreb Trnje",
                "GymCore Zagreb Maksimir", "GymCore Zagreb Dubrava"
        };
        String[] zagrebAddresses = {
                "Ilica 10", "Slavonska avenija 5", "Maksimirska 100",
                "Dubravska 25"
        };

        for (int i = 0; i < zagrebNames.length; i++) {
            locations.add(createLocation(tenant, zagrebNames[i],
                    zagrebAddresses[i], "Zagreb",
                    random.nextInt(200) + 300,
                    random.nextInt(50) + 80));
        }

        // Split lokacije (3)
        String[] splitNames = {
                "GymCore Split Spinut", "GymCore Split Meje"
        };
        String[] splitAddresses = {
                "Spinutska 10", "Mejska 5"
        };

        for (int i = 0; i < splitNames.length; i++) {
            locations.add(createLocation(tenant, splitNames[i],
                    splitAddresses[i], "Split",
                    random.nextInt(150) + 250,
                    random.nextInt(40) + 60));
        }

        // Rijeka lokacije (2)
        String[] rjekaNames = {
                "GymCore Rijeka Centar"
        };
        String[] rjekaAddresses = {
                "Korzo 5"
        };

        for (int i = 0; i < rjekaNames.length; i++) {
            locations.add(createLocation(tenant, rjekaNames[i],
                    rjekaAddresses[i], "Rijeka",
                    random.nextInt(150) + 200,
                    random.nextInt(40) + 50));
        }

        return locationRepository.saveAll(locations);
    }

    private Location createLocation(Tenant tenant, String name,
                                    String address, String city, int area, int capacity) {
        Location location = new Location();
        location.setTenant(tenant);
        location.setName(name);
        location.setAddress(address);
        location.setCity(city);
        location.setTotalAreaM2(area);
        location.setCapacity(capacity);
        location.setEmail(name.toLowerCase().replace(" ", ".") + "@gymcore.hr");
        location.setActive(true);
        return location;
    }

    private List<com.gymcore.entity.Zone> seedZones(Location location) {
        List<com.gymcore.entity.Zone> zones = new ArrayList<>();
        ZoneType[] types = ZoneType.values();

        for (ZoneType type : types) {
            com.gymcore.entity.Zone zone = new com.gymcore.entity.Zone();
            zone.setLocation(location);
            zone.setName(formatZoneName(type));
            zone.setType(type);
            zone.setAreaM2(location.getTotalAreaM2() / types.length);
            zone.setCapacity(location.getCapacity() / types.length);
            zones.add(zone);
        }

        return zoneRepository.saveAll(zones);
    }

    private String formatZoneName(ZoneType type) {
        return switch (type) {
            case FREE_WEIGHTS -> "Free Weights";
            case MACHINES -> "Machines";
            case CABLES -> "Cables";
            case SQUAT -> "Squat Zone";
            case CARDIO -> "Cardio Zone";
            case GROUP -> "Group Training";
            case STRETCHING -> "Stretching";
        };
    }

    private void seedEquipment(com.gymcore.entity.Zone zone) {
        String[][] equipmentByZone = getEquipmentForZone(zone.getType());
        List<Equipment> equipmentList = new ArrayList<>();

        for (String[] eq : equipmentByZone) {
            Equipment equipment = new Equipment();
            equipment.setZone(zone);
            equipment.setName(eq[0]);
            equipment.setBrand(eq[1]);
            equipment.setQuantity(Integer.parseInt(eq[2]));
            equipment.setStatus(EquipmentStatus.OPERATIONAL);
            equipment.setPurchaseDate(LocalDate.now().minusMonths(
                    random.nextInt(24) + 6));
            equipmentList.add(equipment);
        }

        equipmentRepository.saveAll(equipmentList);
    }

    private String[][] getEquipmentForZone(ZoneType type) {
        return switch (type) {
            case FREE_WEIGHTS -> new String[][]{
                    {"Dumbbell Set", "Technogym", "1"},
                    {"Bench Press", "Life Fitness", String.valueOf(random.nextInt(3) + 2)},
                    {"EZ Bar", "Hammer Strength", "3"},
                    {"Olympic Bar", "Eleiko", "4"},
                    {"Weight Plates", "Technogym", "1"},
                    {"Preacher Curl Bench", "Life Fitness", "2"},
                    {"Incline Bench", "Hammer Strength", String.valueOf(random.nextInt(2) + 1)}
            };
            case MACHINES -> new String[][]{
                    {"Leg Press", "Technogym", String.valueOf(random.nextInt(2) + 2)},
                    {"Lat Pulldown", "Life Fitness", String.valueOf(random.nextInt(2) + 2)},
                    {"Chest Press", "Hammer Strength", String.valueOf(random.nextInt(2) + 1)},
                    {"Shoulder Press", "Technogym", String.valueOf(random.nextInt(2) + 1)},
                    {"Leg Curl", "Life Fitness", String.valueOf(random.nextInt(2) + 1)},
                    {"Leg Extension", "Technogym", String.valueOf(random.nextInt(2) + 1)}
            };
            case CABLES -> new String[][]{
                    {"Cable Crossover", "Life Fitness", String.valueOf(random.nextInt(2) + 2)},
                    {"Functional Trainer", "Technogym", String.valueOf(random.nextInt(2) + 1)},
                    {"Low Pulley", "Hammer Strength", "2"},
                    {"High Pulley", "Life Fitness", "2"}
            };
            case SQUAT -> new String[][]{
                    {"Squat Rack", "Eleiko", String.valueOf(random.nextInt(3) + 2)},
                    {"Power Rack", "Hammer Strength", String.valueOf(random.nextInt(2) + 1)},
                    {"Smith Machine", "Technogym", String.valueOf(random.nextInt(2) + 1)},
                    {"Hip Thrust Bench", "Life Fitness", "2"}
            };
            case CARDIO -> new String[][]{
                    {"Treadmill", "Technogym", String.valueOf(random.nextInt(4) + 4)},
                    {"Elliptical Trainer", "Life Fitness", String.valueOf(random.nextInt(3) + 3)},
                    {"Stationary Bike", "Technogym", String.valueOf(random.nextInt(3) + 3)},
                    {"Rowing Machine", "Concept2", String.valueOf(random.nextInt(2) + 2)},
                    {"Stair Climber", "Life Fitness", String.valueOf(random.nextInt(2) + 1)}
            };
            case GROUP -> new String[][]{
                    {"Spinning Bike", "Technogym", String.valueOf(random.nextInt(5) + 10)},
                    {"Yoga Mat", "Reebok", String.valueOf(random.nextInt(10) + 15)},
                    {"Kettlebell Set", "Hammer Strength", "1"},
                    {"TRX System", "TRX", String.valueOf(random.nextInt(3) + 3)}
            };
            case STRETCHING -> new String[][]{
                    {"Exercise Mat", "Reebok", String.valueOf(random.nextInt(5) + 10)},
                    {"Foam Roller", "Technogym", String.valueOf(random.nextInt(5) + 5)},
                    {"Gym Ball", "Technogym", String.valueOf(random.nextInt(3) + 3)},
                    {"Stretching Station", "Life Fitness", "2"}
            };
        };
    }

    private List<Member> seedMembers(Location location, Tenant tenant) {
        List<Member> members = new ArrayList<>();
        int count = random.nextInt(70) + 50;

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setFullName(faker.name().fullName());
            user.setRole(Role.MEMBER);
            user.setTenant(tenant);
            user.setActive(true);
            User savedUser = userRepository.save(user);

            Member member = new Member();
            member.setUser(savedUser);
            member.setLocation(location);
            member.setMembershipStartDate(
                    LocalDate.now().minusMonths(random.nextInt(12) + 1));
            member.setActive(true);
            members.add(memberRepository.save(member));
        }

        return members;
    }

    private void seedCheckIns(List<Member> members, Location location) {
        List<CheckIn> checkIns = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        int[] peakHours = {7, 8, 9, 17, 18, 19, 20};
        int[] offPeakHours = {10, 11, 12, 13, 14, 15, 16};

        for (Member member : members) {
            int visits = random.nextInt(40) + 40;

            for (int i = 0; i < visits; i++) {
                LocalDateTime checkInTime = now.minusDays(random.nextInt(90));

                int hour;
                if (random.nextDouble() < 0.7) {
                    hour = peakHours[random.nextInt(peakHours.length)];
                } else {
                    hour = offPeakHours[random.nextInt(offPeakHours.length)];
                }

                checkInTime = checkInTime.withHour(hour)
                        .withMinute(random.nextInt(60));

                int durationMinutes = random.nextInt(60) + 45;
                LocalDateTime checkOutTime = checkInTime
                        .plusMinutes(durationMinutes);

                CheckIn checkIn = new CheckIn();
                checkIn.setMember(member);
                checkIn.setLocation(location);
                checkIn.setCheckInTime(checkInTime);
                checkIn.setCheckOutTime(checkOutTime);
                checkIns.add(checkIn);
            }
        }

        checkInRepository.saveAll(checkIns);
        log.info("Seeded {} check-ins for location: {}",
                checkIns.size(), location.getName());
    }
}
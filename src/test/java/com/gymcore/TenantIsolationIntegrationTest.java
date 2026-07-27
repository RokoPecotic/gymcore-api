package com.gymcore;

import com.gymcore.dto.AuthResponse;
import com.gymcore.dto.LocationRequest;
import com.gymcore.dto.RegisterRequest;
import com.gymcore.entity.Role;
import com.gymcore.entity.SubscriptionPlan;
import com.gymcore.entity.Tenant;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.TenantRepository;
import com.gymcore.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class TenantIsolationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    private Long tenantAId;
    private Long tenantBId;

    @BeforeEach
    void setUp() {
        Tenant tenantA = new Tenant();
        tenantA.setName("Gym Chain A");
        tenantA.setSubdomain("gym-a");
        tenantA.setContactEmail("a@gym.com");
        tenantA.setSubscriptionPlan(SubscriptionPlan.PRO);
        tenantA.setActive(true);
        tenantAId = tenantRepository.save(tenantA).getId();

        Tenant tenantB = new Tenant();
        tenantB.setName("Gym Chain B");
        tenantB.setSubdomain("gym-b");
        tenantB.setContactEmail("b@gym.com");
        tenantB.setSubscriptionPlan(SubscriptionPlan.PRO);
        tenantB.setActive(true);
        tenantBId = tenantRepository.save(tenantB).getId();
    }

    @AfterEach
    void tearDown() {
        locationRepository.deleteAll();
        userRepository.deleteAll();
        tenantRepository.deleteAll();
    }

    @Test
    void shouldNotSeeOtherTenantsLocations() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RegisterRequest registerA = new RegisterRequest();
        registerA.setEmail("usera@test.com");
        registerA.setPassword("password123");
        registerA.setFullName("User A");
        registerA.setRole(Role.GYM_MANAGER);
        registerA.setTenantId(tenantAId);

        ResponseEntity<AuthResponse> authResponseA = restTemplate.postForEntity(
                "/api/auth/register", new HttpEntity<>(registerA, headers), AuthResponse.class);
        String tokenA = authResponseA.getBody().getToken();

        HttpHeaders headersA = new HttpHeaders();
        headersA.setContentType(MediaType.APPLICATION_JSON);
        headersA.setBearerAuth(tokenA);

        LocationRequest locationA = new LocationRequest();
        locationA.setName("Gym A Location");
        locationA.setAddress("Address A");
        locationA.setCity("Zagreb");
        locationA.setTotalAreaM2(300);
        locationA.setCapacity(100);
        locationA.setEmail("locationa@gym.com");
        locationA.setTenantId(tenantAId);

        ResponseEntity<String> responseA = restTemplate.postForEntity(
                "/api/locations", new HttpEntity<>(locationA, headersA), String.class);
        assertThat(responseA.getStatusCode()).isEqualTo(HttpStatus.OK);

        RegisterRequest registerB = new RegisterRequest();
        registerB.setEmail("userb@test.com");
        registerB.setPassword("password123");
        registerB.setFullName("User B");
        registerB.setRole(Role.GYM_MANAGER);
        registerB.setTenantId(tenantBId);

        ResponseEntity<AuthResponse> authResponseB = restTemplate.postForEntity(
                "/api/auth/register", new HttpEntity<>(registerB, headers), AuthResponse.class);
        String tokenB = authResponseB.getBody().getToken();

        HttpHeaders headersB = new HttpHeaders();
        headersB.setContentType(MediaType.APPLICATION_JSON);
        headersB.setBearerAuth(tokenB);

        LocationRequest locationB = new LocationRequest();
        locationB.setName("Gym B Location");
        locationB.setAddress("Address B");
        locationB.setCity("Split");
        locationB.setTotalAreaM2(250);
        locationB.setCapacity(80);
        locationB.setEmail("locationb@gym.com");
        locationB.setTenantId(tenantBId);

        ResponseEntity<String> responseB = restTemplate.postForEntity(
                "/api/locations", new HttpEntity<>(locationB, headersB), String.class);
        assertThat(responseB.getStatusCode()).isEqualTo(HttpStatus.OK);

        var tenantALocations = locationRepository.findByTenantId(tenantAId);
        var tenantBLocations = locationRepository.findByTenantId(tenantBId);

        assertThat(tenantALocations).hasSize(1);
        assertThat(tenantALocations.get(0).getName()).isEqualTo("Gym A Location");

        assertThat(tenantBLocations).hasSize(1);
        assertThat(tenantBLocations.get(0).getName()).isEqualTo("Gym B Location");

        assertThat(tenantALocations)
                .extracting("name")
                .doesNotContain("Gym B Location");

        assertThat(tenantBLocations)
                .extracting("name")
                .doesNotContain("Gym A Location");
    }
}
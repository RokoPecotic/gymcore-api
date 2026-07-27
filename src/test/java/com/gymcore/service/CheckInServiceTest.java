package com.gymcore.service;

import com.gymcore.dto.CheckInRequest;
import com.gymcore.dto.CheckInResponse;
import com.gymcore.dto.OccupancyResponse;
import com.gymcore.entity.CheckIn;
import com.gymcore.entity.Location;
import com.gymcore.entity.Member;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.CheckInRepository;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private CheckInService checkInService;

    private Member member;
    private Location location;
    private CheckIn checkIn;
    private CheckInRequest request;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setId(1L);
        location.setName("GymCore Split Spinut");
        location.setCapacity(120);

        member = new Member();
        member.setId(2L);

        checkIn = new CheckIn();
        checkIn.setId(1L);
        checkIn.setMember(member);
        checkIn.setLocation(location);
        checkIn.setCheckInTime(LocalDateTime.now());

        request = new CheckInRequest();
        request.setMemberId(2L);
        request.setLocationId(1L);
    }

    @Test
    @DisplayName("Should check in member when not already checked in")
    void shouldCheckIn_whenMemberNotAlreadyCheckedIn() {
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(checkInRepository.findByMemberIdAndCheckOutTimeIsNull(2L))
                .thenReturn(Optional.empty());
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(checkIn);

        CheckInResponse result = checkInService.checkIn(request);

        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(2L);
        assertThat(result.getLocationId()).isEqualTo(1L);
        assertThat(result.getCheckOutTime()).isNull();
        verify(checkInRepository).save(any(CheckIn.class));
    }

    @Test
    @DisplayName("Should throw exception when member not found")
    void shouldThrowException_whenMemberNotFound() {
        when(memberRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.checkIn(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found");

        verify(checkInRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when member already checked in")
    void shouldThrowException_whenMemberAlreadyCheckedIn() {
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(checkInRepository.findByMemberIdAndCheckOutTimeIsNull(2L))
                .thenReturn(Optional.of(checkIn));

        assertThatThrownBy(() -> checkInService.checkIn(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already checked in");

        verify(checkInRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should check out member when checked in")
    void shouldCheckOut_whenMemberIsCheckedIn() {
        when(checkInRepository.findByMemberIdAndCheckOutTimeIsNull(2L))
                .thenReturn(Optional.of(checkIn));
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(checkIn);

        CheckInResponse result = checkInService.checkOut(2L);

        assertThat(result).isNotNull();
        verify(checkInRepository).save(any(CheckIn.class));
    }

    @Test
    @DisplayName("Should throw exception when checking out member not checked in")
    void shouldThrowException_whenCheckingOutMemberNotCheckedIn() {
        when(checkInRepository.findByMemberIdAndCheckOutTimeIsNull(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.checkOut(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not checked in");

        verify(checkInRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should calculate occupancy correctly")
    void shouldCalculateOccupancy_correctly() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(checkInRepository.countByLocationIdAndCheckOutTimeIsNull(1L))
                .thenReturn(12L);

        OccupancyResponse result = checkInService.getOccupancy(1L);

        assertThat(result.getLocationId()).isEqualTo(1L);
        assertThat(result.getCurrentOccupancy()).isEqualTo(12);
        assertThat(result.getCapacity()).isEqualTo(120);
        assertThat(result.getOccupancyPercentage()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Should throw exception when getting occupancy for non-existent location")
    void shouldThrowException_whenLocationNotFoundForOccupancy() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.getOccupancy(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found");
    }
}
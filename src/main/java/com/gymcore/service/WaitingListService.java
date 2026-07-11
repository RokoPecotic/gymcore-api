package com.gymcore.service;

import com.gymcore.dto.WaitingListRequest;
import com.gymcore.dto.WaitingListResponse;
import com.gymcore.entity.Location;
import com.gymcore.entity.Member;
import com.gymcore.entity.WaitingList;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.MemberRepository;
import com.gymcore.repository.WaitingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingListService {

    private final WaitingListRepository waitingListRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;

    public WaitingListResponse joinWaitingList(WaitingListRequest request) {

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with id " + request.getMemberId()
                ));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id " + request.getLocationId()
                ));

        waitingListRepository.findByMemberIdAndLocationIdAndActive(request.getMemberId(), request.getLocationId(), true)
                .ifPresent(c -> {throw new DuplicateResourceException(
                        "Member already checked in");
                });

        WaitingList waitingList = new WaitingList();
        waitingList.setMember(member);
        waitingList.setLocation(location);
        waitingList.setActive(true);

        return toResponse(waitingListRepository.save(waitingList));
    }

    private WaitingListResponse toResponse(WaitingList wl) {
        return new WaitingListResponse(
                wl.getId(),
                wl.getMember().getId(),
                wl.getLocation().getId(),
                wl.getLocation().getName(),
                wl.getActive(),
                wl.getCreatedAt()
        );
    }

    public WaitingListResponse leaveWaitingList(Long memberId, Long locationId) {
        WaitingList waitingList = waitingListRepository
                .findByMemberIdAndLocationIdAndActive(memberId, locationId, true)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member is not on waiting list"));

        waitingList.setActive(false);
        return toResponse(waitingListRepository.save(waitingList));
    }

    public List<WaitingListResponse> getWaitingListForLocation(Long locationId) {
        return waitingListRepository.findByLocationIdAndActive(locationId, true)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}

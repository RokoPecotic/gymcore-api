package com.gymcore.repository;

import com.gymcore.entity.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaitingListRepository extends JpaRepository<WaitingList, Long> {
    List<WaitingList> findByLocationIdAndActive(Long locationId, Boolean active);
    Optional<WaitingList> findByMemberIdAndLocationIdAndActive(Long memberId, Long locationId, Boolean active);
    boolean existsByMemberIdAndLocationIdAndActive(Long memberId, Long locationId, Boolean active);
}

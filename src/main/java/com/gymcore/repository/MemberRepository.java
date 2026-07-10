package com.gymcore.repository;

import com.gymcore.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByLocationId(Long locationId);
    boolean existsByUserId(Long userId);
}
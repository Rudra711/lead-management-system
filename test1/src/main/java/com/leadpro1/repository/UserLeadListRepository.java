package com.leadpro1.repository;

import com.leadpro1.entity.UserLeadList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLeadListRepository extends JpaRepository<UserLeadList, UUID> {

    List<UserLeadList> findByUserId(UUID userId);

    boolean existsByUserIdAndLeadListId(UUID userId, UUID leadListId);
    Optional<UserLeadList> findByUserIdAndLeadListId(UUID userId, UUID leadListId);
}
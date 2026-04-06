package com.leadpro1.repository;

import com.leadpro1.entity.LeadList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeadListRepository extends JpaRepository<LeadList, UUID> {
}
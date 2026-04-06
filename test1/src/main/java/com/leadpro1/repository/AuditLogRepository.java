package com.leadpro1.repository;

import com.leadpro1.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // 🔥 Get logs for a specific entity (like a Lead)
    List<AuditLog> findByEntityIdOrderByTimestampDesc(UUID entityId);

    // 🔥 Optional: filter by entity type (LEAD, LEAD_LIST)
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
            String entityType,
            UUID entityId
    );
}
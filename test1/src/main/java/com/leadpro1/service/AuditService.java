package com.leadpro1.service;

import com.leadpro1.dto.AuditResponse;
import com.leadpro1.entity.AuditLog;
import com.leadpro1.entity.Lead;
import com.leadpro1.entity.Permission;
import com.leadpro1.repository.AuditLogRepository;
import com.leadpro1.repository.LeadRepository;
import com.leadpro1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableAsync
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final LeadRepository     leadRepository;
    private final LeadListService    leadListService;
    private final UserRepository     userRepository;   // ← NEW: to resolve userId → email

    @Async
    public void logChanges(List<AuditLog> logs) {
        auditLogRepository.saveAll(logs);
    }

    public List<AuditResponse> getLeadAudit(UUID userId, UUID leadId) {

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        leadListService.validateAccess(userId, lead.getLeadListId());
        leadListService.checkPermission(userId, lead.getLeadListId(), Permission.INVITE);

        return auditLogRepository
                .findByEntityTypeAndEntityIdOrderByTimestampDesc("LEAD", leadId)
                .stream()
                .map(log -> {
                    // Resolve the email of whoever made this change
                    String email = userRepository.findById(log.getUserId())
                            .map(u -> u.getEmail())
                            .orElse("Unknown");

                    return AuditResponse.builder()
                            .field(log.getFieldName())
                            .oldValue(log.getOldValue())
                            .newValue(log.getNewValue())
                            .action(log.getAction())
                            .timestamp(log.getTimestamp())
                            .changedByEmail(email)
                            .build();
                })
                .toList();
    }
}
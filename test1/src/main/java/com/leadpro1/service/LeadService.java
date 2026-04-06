package com.leadpro1.service;

import com.leadpro1.dto.*;
import com.leadpro1.entity.*;
import com.leadpro1.repository.LeadRepository;
import com.leadpro1.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadListService leadListService;
    private final AuditService auditService;
    // 🔥 ENTITY → DTO
    private LeadResponse mapToResponse(Lead lead) {
        return LeadResponse.builder()
                .id(lead.getId())
                .name(lead.getName())
                .phone(lead.getPhone())
                .email(lead.getEmail())
                .address(lead.getAddress())
                .dob(lead.getDob())
                .gender(lead.getGender())
                .annualIncome(lead.getAnnualIncome())
                .inNeed(lead.getInNeed())
                .status(lead.getStatus())
                .build();
    }

    // 🔥 CREATE
    public LeadResponse createLead(UUID userId, UUID leadListId, CreateLeadRequest req) {

        leadListService.validateAccess(userId, leadListId);
        leadListService.checkPermission(userId, leadListId, Permission.CREATE);

        Lead lead = Lead.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .dob(req.getDob())
                .gender(req.getGender())
                .annualIncome(req.getAnnualIncome())
                .inNeed(req.getInNeed())
                .status(req.getStatus() != null ? req.getStatus() : LeadStatus.NEW)
                .leadListId(leadListId)
                .createdBy(userId)
                .build();
        auditService.logChanges(List.of(
                AuditLog.builder()
                        .userId(userId)
                        .entityType("LEAD")
                        .entityId(lead.getId())
                        .fieldName("ALL")
                        .oldValue(null)
                        .newValue("Created")
                        .action("CREATE")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
        return mapToResponse(leadRepository.save(lead));
    }

    // 🔥 UPDATE
    public LeadResponse updateLead(UUID userId, UUID leadId, UpdateLeadRequest req) {

        Lead oldLead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        leadListService.validateAccess(userId, oldLead.getLeadListId());
        leadListService.checkPermission(userId, oldLead.getLeadListId(), Permission.EDIT);

        // 🔥 Create copy for comparison
        Lead newLead = Lead.builder()
                .id(oldLead.getId())
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .dob(req.getDob())
                .gender(req.getGender())
                .annualIncome(req.getAnnualIncome())
                .inNeed(req.getInNeed())
                .status(req.getStatus())
                .leadListId(oldLead.getLeadListId())
                .createdBy(oldLead.getCreatedBy())
                .build();

        // 🔍 Detect changes
        Map<String, Object[]> changes = AuditUtil.getChanges(oldLead, newLead);

        // 🔄 Apply changes
        oldLead.setName(req.getName());
        oldLead.setPhone(req.getPhone());
        oldLead.setEmail(req.getEmail());
        oldLead.setAddress(req.getAddress());
        oldLead.setDob(req.getDob());
        oldLead.setGender(req.getGender());
        oldLead.setAnnualIncome(req.getAnnualIncome());
        oldLead.setInNeed(req.getInNeed());
        oldLead.setStatus(req.getStatus());

        Lead saved = leadRepository.save(oldLead);

        // 🔥 Build audit logs
        List<AuditLog> logs = changes.entrySet().stream()
                .map(entry -> AuditLog.builder()
                        .userId(userId)
                        .entityType("LEAD")
                        .entityId(saved.getId())
                        .fieldName(entry.getKey())
                        .oldValue(String.valueOf(entry.getValue()[0]))
                        .newValue(String.valueOf(entry.getValue()[1]))
                        .action("UPDATE")
                        .timestamp(LocalDateTime.now())
                        .build()
                )
                .toList();

        // 🚀 Async save
        auditService.logChanges(logs);

        return mapToResponse(saved);
    }

    // 🔥 DELETE
    public void deleteLead(UUID userId, UUID leadId) {

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));

        leadListService.validateAccess(userId, lead.getLeadListId());
        leadListService.checkPermission(userId, lead.getLeadListId(), Permission.DELETE);
        auditService.logChanges(List.of(
                AuditLog.builder()
                        .userId(userId)
                        .entityType("LEAD")
                        .entityId(lead.getId())
                        .fieldName("ALL")
                        .oldValue("Existing Data")
                        .newValue(null)
                        .action("DELETE")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
        leadRepository.delete(lead);
    }

    // 🔥 GET ALL
    public List<LeadResponse> getLeads(UUID userId, UUID leadListId) {

        leadListService.validateAccess(userId, leadListId);
        leadListService.checkPermission(userId, leadListId, Permission.VIEW);

        return leadRepository.findByLeadListId(leadListId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<LeadResponse> getLeads(
            UUID userId,
            UUID leadListId,
            String search,
            LeadStatus status,
            Boolean inNeed
    ) {

        // 🔐 Security
        leadListService.validateAccess(userId, leadListId);
        leadListService.checkPermission(userId, leadListId, Permission.VIEW);

        // status must be passed as String (not enum) because the repository
        // uses a native SQL query — JPA won't auto-convert enums there.
        String statusStr = (status != null) ? status.name() : null;

        return leadRepository
                .searchAndFilter(leadListId, search, statusStr, inNeed)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
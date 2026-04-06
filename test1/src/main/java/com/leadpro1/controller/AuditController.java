package com.leadpro1.controller;

import com.leadpro1.dto.AuditResponse;
import com.leadpro1.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/leads")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/{leadId}/audit")
    public List<AuditResponse> getAudit(
            @PathVariable UUID leadId,
            HttpServletRequest request
    ) {

        UUID userId = (UUID) request.getAttribute("userId");

        return auditService.getLeadAudit(userId, leadId);
    }
}
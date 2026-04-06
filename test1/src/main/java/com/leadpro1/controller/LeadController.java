package com.leadpro1.controller;

import com.leadpro1.dto.*;
import com.leadpro1.entity.LeadStatus;
import com.leadpro1.service.LeadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/leadlists/{leadListId}/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    // 🔥 CREATE
    @PostMapping
    public LeadResponse create(@PathVariable UUID leadListId,
                               @RequestBody CreateLeadRequest request,
                               HttpServletRequest httpRequest) {

        UUID userId = (UUID) httpRequest.getAttribute("userId");

        return leadService.createLead(userId, leadListId, request);
    }

    // 🔥 UPDATE
    @PutMapping("/{leadId}")
    public LeadResponse update(@PathVariable UUID leadListId,
                               @PathVariable UUID leadId,
                               @RequestBody UpdateLeadRequest request,
                               HttpServletRequest httpRequest) {

        UUID userId = (UUID) httpRequest.getAttribute("userId");

        return leadService.updateLead(userId, leadId, request);
    }

    // 🔥 DELETE
    @DeleteMapping("/{leadId}")
    public ResponseEntity<String> delete(@PathVariable UUID leadListId,
                                         @PathVariable UUID leadId,
                                         HttpServletRequest request) {

        UUID userId = (UUID) request.getAttribute("userId");

        leadService.deleteLead(userId, leadId);

        return ResponseEntity.ok("Lead deleted successfully");
    }
    @GetMapping
    public List<LeadResponse> getAll(
            @PathVariable UUID leadListId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) Boolean inNeed,
            HttpServletRequest request
    ) {

        UUID userId = (UUID) request.getAttribute("userId");

        return leadService.getLeads(
                userId,
                leadListId,
                search,
                status,
                inNeed
        );
    }
}
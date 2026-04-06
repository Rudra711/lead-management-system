package com.leadpro1.controller;

import com.leadpro1.dto.InvitationResponse;
import com.leadpro1.dto.InviteRequest;
import com.leadpro1.service.InviteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    // ── OWNER sends invite to a user by email ─────────────────────────────────
    // POST /leadlists/{leadListId}/invite
    @PostMapping("/leadlists/{leadListId}/invite")
    public ResponseEntity<String> inviteUser(
            @PathVariable UUID leadListId,
            @RequestBody InviteRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID userId = (UUID) httpRequest.getAttribute("userId");
        inviteService.inviteUser(userId, leadListId, request.getEmail(), request.getRole());
        return ResponseEntity.ok("Invitation sent successfully");
    }

    // ── Get my pending invitations ────────────────────────────────────────────
    // GET /invitations
    @GetMapping("/invitations")
    public List<InvitationResponse> getMyInvitations(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return inviteService.getPendingInvitations(userId);
    }

    // ── Accept an invitation ──────────────────────────────────────────────────
    // POST /invitations/{id}/accept
    @PostMapping("/invitations/{id}/accept")
    public ResponseEntity<String> accept(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        UUID userId = (UUID) request.getAttribute("userId");
        inviteService.acceptInvitation(userId, id);
        return ResponseEntity.ok("Invitation accepted");
    }

    // ── Decline an invitation ─────────────────────────────────────────────────
    // POST /invitations/{id}/decline
    @PostMapping("/invitations/{id}/decline")
    public ResponseEntity<String> decline(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        UUID userId = (UUID) request.getAttribute("userId");
        inviteService.declineInvitation(userId, id);
        return ResponseEntity.ok("Invitation declined");
    }
}
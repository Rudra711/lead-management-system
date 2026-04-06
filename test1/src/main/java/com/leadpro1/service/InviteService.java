package com.leadpro1.service;

import com.leadpro1.dto.InvitationResponse;
import com.leadpro1.entity.*;
import com.leadpro1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final UserRepository          userRepository;
    private final UserLeadListRepository  userLeadListRepository;
    private final InvitationRepository    invitationRepository;
    private final LeadListRepository      leadListRepository;
    private final LeadListService         leadListService;

    // ── SEND INVITE ───────────────────────────────────────────────
    public void inviteUser(UUID ownerId, UUID leadListId, String email, Role role) {

        // Only OWNER can invite
        leadListService.checkPermission(ownerId, leadListId, Permission.INVITE);

        // Cannot assign OWNER role
        if (role == Role.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot assign OWNER role");
        }

        // Find the invitee by their registered email
        User invitee = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No user found with that email address"));

        // Cannot invite yourself
        if (invitee.getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot invite yourself");
        }

        // If invitee already has access, just update their role directly
        Optional<UserLeadList> existingAccess = userLeadListRepository
                .findByUserIdAndLeadListId(invitee.getId(), leadListId);

        if (existingAccess.isPresent()) {
            existingAccess.get().setRole(role);
            userLeadListRepository.save(existingAccess.get());
            return;
        }

        // If a PENDING invite already exists, update its role
        Optional<Invitation> existingPending = invitationRepository
                .findByInviteeIdAndLeadListIdAndStatus(
                        invitee.getId(), leadListId, InvitationStatus.PENDING);

        if (existingPending.isPresent()) {
            existingPending.get().setRole(role);
            invitationRepository.save(existingPending.get());
            return;
        }

        // Create a fresh PENDING invitation
        Invitation inv = Invitation.builder()
                .inviteeId(invitee.getId())
                .inviterId(ownerId)
                .leadListId(leadListId)
                .role(role)
                .status(InvitationStatus.PENDING)
                .build();
        invitationRepository.save(inv);
    }

    // ── GET PENDING INVITATIONS ───────────────────────────────────
    public List<InvitationResponse> getPendingInvitations(UUID userId) {

        return invitationRepository
                .findByInviteeIdAndStatus(userId, InvitationStatus.PENDING)
                .stream()
                .map(inv -> {
                    String listName = leadListRepository.findById(inv.getLeadListId())
                            .map(LeadList::getName)
                            .orElse("Unknown List");

                    String inviterEmail = userRepository.findById(inv.getInviterId())
                            .map(User::getEmail)
                            .orElse("Unknown");

                    return InvitationResponse.builder()
                            .id(inv.getId())
                            .leadListName(listName)
                            .invitedByEmail(inviterEmail)
                            .role(inv.getRole())
                            .createdAt(inv.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── ACCEPT ────────────────────────────────────────────────────
    public void acceptInvitation(UUID userId, UUID invitationId) {

        Invitation inv = getAndValidate(userId, invitationId);

        // Grant access
        UserLeadList mapping = UserLeadList.builder()
                .userId(userId)
                .leadListId(inv.getLeadListId())
                .role(inv.getRole())
                .build();
        userLeadListRepository.save(mapping);

        inv.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(inv);
    }

    // ── DECLINE ───────────────────────────────────────────────────
    public void declineInvitation(UUID userId, UUID invitationId) {

        Invitation inv = getAndValidate(userId, invitationId);
        inv.setStatus(InvitationStatus.DECLINED);
        invitationRepository.save(inv);
    }

    // ── HELPER ────────────────────────────────────────────────────
    private Invitation getAndValidate(UUID userId, UUID invitationId) {

        Invitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Invitation not found"));

        if (!inv.getInviteeId().equals(userId)) {
            throw new AccessDeniedException("This invitation does not belong to you");
        }

        if (inv.getStatus() != InvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invitation already " + inv.getStatus().name().toLowerCase());
        }

        return inv;
    }
}
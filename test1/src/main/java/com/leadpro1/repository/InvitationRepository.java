package com.leadpro1.repository;

import com.leadpro1.entity.Invitation;
import com.leadpro1.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    // All PENDING invitations for a user (shown in Invitations page)
    List<Invitation> findByInviteeIdAndStatus(UUID inviteeId, InvitationStatus status);

    // Check if an identical pending invite already exists (avoid duplicates)
    Optional<Invitation> findByInviteeIdAndLeadListIdAndStatus(
            UUID inviteeId, UUID leadListId, InvitationStatus status);
}
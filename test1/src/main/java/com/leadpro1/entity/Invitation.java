package com.leadpro1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue
    private UUID id;

    // Who is being invited (looked up by email → user.id)
    private UUID inviteeId;

    // Who sent the invite (the OWNER)
    private UUID inviterId;

    private UUID leadListId;

    @Enumerated(EnumType.STRING)
    private Role role;           // EDITOR or VIEWER

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
package com.leadpro1.dto;

import com.leadpro1.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InvitationResponse {

    private UUID id;
    private String leadListName;   // name of the lead list being shared
    private String invitedByEmail; // email of the owner who sent the invite
    private Role role;             // EDITOR or VIEWER
    private LocalDateTime createdAt;
}
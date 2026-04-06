package com.leadpro1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLeadList {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;
    private UUID leadListId;

    @Enumerated(EnumType.STRING)
    private Role role;
}
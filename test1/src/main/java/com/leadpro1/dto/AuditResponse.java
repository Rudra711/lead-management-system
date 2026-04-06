package com.leadpro1.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditResponse {

    private String field;
    private String oldValue;
    private String newValue;
    private String action;
    private LocalDateTime timestamp;
    private String changedByEmail;   // ← NEW: email of the user who made the change
}
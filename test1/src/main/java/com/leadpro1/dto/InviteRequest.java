package com.leadpro1.dto;
import com.leadpro1.entity.Role;
import lombok.Data;

@Data
public class InviteRequest {
    private String email;
    private Role role;
}
package com.leadpro1.dto;

import com.leadpro1.entity.Gender;
import com.leadpro1.entity.LeadStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class LeadResponse {

    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate dob;
    private Gender gender;
    private Double annualIncome;
    private Boolean inNeed;
    private LeadStatus status;
}
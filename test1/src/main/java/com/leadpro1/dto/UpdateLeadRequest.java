package com.leadpro1.dto;

import com.leadpro1.entity.Gender;
import com.leadpro1.entity.LeadStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateLeadRequest {

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

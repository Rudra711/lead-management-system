package com.leadpro1.entity;

import com.leadpro1.entity.Gender;
import com.leadpro1.entity.LeadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String phone;
    private String email;
    private String address;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double annualIncome;

    private Boolean inNeed;

    // 🔥 NEW FIELDS

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    private UUID createdBy;          // ✅ recommended
    private String createdByEmail;   // optional

    private UUID leadListId;
}
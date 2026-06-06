package com.hms.user_service.dto.response;

import com.hms.user_service.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {
    private Long id;
    private Long authId;
    private String name;
    private String email;
    private String phone;
    private String speciality;
    private String licenseNumber;
    private Integer experience;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

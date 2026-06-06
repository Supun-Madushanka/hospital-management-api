package com.hms.user_service.dto.request;

import com.hms.user_service.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorApprovalRequest {

    @NotNull(message = "Approval status is required")
    private ApprovalStatus approvalStatus;
}

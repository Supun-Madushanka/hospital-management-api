package com.hms.appointment_service.dto.request;

import com.hms.appointment_service.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentStatusRequest {

    @NotNull(message = "Status is required")
    private AppointmentStatus status;

    private String notes;
}

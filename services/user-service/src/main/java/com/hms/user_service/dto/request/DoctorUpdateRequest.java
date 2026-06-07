package com.hms.user_service.dto.request;

import lombok.Data;

@Data
public class DoctorUpdateRequest {
    private String name;
    private String phone;
    private String speciality;
    private Integer experience;
}
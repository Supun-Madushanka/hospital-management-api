package com.hms.user_service.dto.request;

import com.hms.user_service.enums.Gender;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientUpdateRequest {
    private String name;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
}
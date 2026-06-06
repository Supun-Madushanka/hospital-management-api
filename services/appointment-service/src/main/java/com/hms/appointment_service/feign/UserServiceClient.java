package com.hms.appointment_service.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/users/doctors/auth/{authId}")
    DoctorResponse getDoctorByAuthId(@PathVariable("authId") Long authId);

    @GetMapping("/api/users/patients/auth/{authId}")
    PatientResponse getPatientByAuthId(@PathVariable("authId") Long authId);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class DoctorResponse {
        private boolean success;
        private String message;
        private DoctorData data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class DoctorData {
        private Long id;
        private Long authId;
        private String name;
        private String email;
        private String speciality;
        private String approvalStatus;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class PatientResponse {
        private boolean success;
        private String message;
        private PatientData data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class PatientData {
        private Long id;
        private Long authId;
        private String name;
        private String email;
    }
}

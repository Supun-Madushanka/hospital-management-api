package com.hms.user_service.service;

import com.hms.user_service.dto.request.PatientRegisterRequest;
import com.hms.user_service.dto.request.PatientUpdateRequest;
import com.hms.user_service.dto.response.PatientResponse;
import java.util.List;

public interface PatientService {
    PatientResponse register(PatientRegisterRequest request);
    PatientResponse getPatientById(Long id);
    PatientResponse getPatientByAuthId(Long authId);
    PatientResponse updatePatient(Long authId, PatientUpdateRequest request);
    void deletePatient(Long authId);
    List<PatientResponse> getAllPatients();
}

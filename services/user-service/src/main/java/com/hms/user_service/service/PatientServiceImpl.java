package com.hms.user_service.service;

import com.hms.user_service.dto.request.PatientRegisterRequest;
import com.hms.user_service.dto.request.PatientUpdateRequest;
import com.hms.user_service.dto.response.PatientResponse;
import com.hms.user_service.entity.Patient;
import com.hms.user_service.exception.ResourceNotFoundException;
import com.hms.user_service.exception.UnauthorizedException;
import com.hms.user_service.feign.AuthServiceClient;
import com.hms.user_service.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    public PatientResponse register(PatientRegisterRequest request) {

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }

        AuthServiceClient.AuthResponse authResponse = authServiceClient.register(
                new AuthServiceClient.AuthRequest(
                        request.getEmail(),
                        request.getPassword(),
                        "PATIENT"
                )
        );

        if (!authResponse.isSuccess()) {
            throw new UnauthorizedException(authResponse.getMessage());
        }

        Patient patient = Patient.builder()
                .authId(authResponse.getData().getId())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .build();

        Patient saved = patientRepository.save(patient);

        return mapToResponse(saved);
    }

    @Override
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return mapToResponse(patient);
    }

    @Override
    public PatientResponse getPatientByAuthId(Long authId) {
        Patient patient = patientRepository.findByAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return mapToResponse(patient);
    }

    @Override
    public PatientResponse updatePatient(Long authId, PatientUpdateRequest request) {
        Patient patient = patientRepository.findByAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (request.getName() != null) patient.setName(request.getName());
        if (request.getPhone() != null) patient.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());

        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    private PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .authId(patient.getAuthId())
                .name(patient.getName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}

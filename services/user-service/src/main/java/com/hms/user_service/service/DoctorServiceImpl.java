package com.hms.user_service.service;

import com.hms.user_service.dto.request.DoctorApprovalRequest;
import com.hms.user_service.dto.request.DoctorRegisterRequest;
import com.hms.user_service.dto.request.DoctorUpdateRequest;
import com.hms.user_service.dto.response.DoctorResponse;
import com.hms.user_service.entity.Doctor;
import com.hms.user_service.enums.ApprovalStatus;
import com.hms.user_service.exception.ResourceNotFoundException;
import com.hms.user_service.exception.UnauthorizedException;
import com.hms.user_service.feign.AuthServiceClient;
import com.hms.user_service.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    public DoctorResponse register(DoctorRegisterRequest request) {

        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }

        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new UnauthorizedException("License number already exists");
        }

        AuthServiceClient.AuthResponse authResponse = authServiceClient.register(
                new AuthServiceClient.AuthRequest(
                        request.getEmail(),
                        request.getPassword(),
                        "DOCTOR"
                )
        );

        if (!authResponse.isSuccess()) {
            throw new UnauthorizedException(authResponse.getMessage());
        }

        Doctor doctor = Doctor.builder()
                .authId(authResponse.getData().getId())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .speciality(request.getSpeciality())
                .licenseNumber(request.getLicenseNumber())
                .experience(request.getExperience())
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        Doctor saved = doctorRepository.save(doctor);

        return mapToResponse(saved);
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return mapToResponse(doctor);
    }

    @Override
    public DoctorResponse getDoctorByAuthId(Long authId) {
        Doctor doctor = doctorRepository.findByAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return mapToResponse(doctor);
    }

    @Override
    public List<DoctorResponse> getPendingDoctors() {
        return doctorRepository.findByApprovalStatus(ApprovalStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse approveOrRejectDoctor(Long id, DoctorApprovalRequest request, Long adminAuthId) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        doctor.setApprovalStatus(request.getApprovalStatus());
        doctor.setApprovedBy(adminAuthId);

        Doctor saved = doctorRepository.save(doctor);

        // Update auth-service status
        String authStatus = request.getApprovalStatus() == ApprovalStatus.APPROVED ? "ACTIVE" : "REJECTED";
        authServiceClient.updateStatus(
                saved.getAuthId(),
                new AuthServiceClient.StatusUpdateRequest(authStatus)
        );

        return mapToResponse(saved);
    }

    @Override
    public DoctorResponse updateDoctor(Long authId, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findByAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (request.getName() != null) doctor.setName(request.getName());
        if (request.getPhone() != null) doctor.setPhone(request.getPhone());
        if (request.getSpeciality() != null) doctor.setSpeciality(request.getSpeciality());
        if (request.getExperience() != null) doctor.setExperience(request.getExperience());

        Doctor saved = doctorRepository.save(doctor);
        return mapToResponse(saved);
    }

    @Override
    public void deleteDoctor(Long authId) {
        Doctor doctor = doctorRepository.findByAuthId(authId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        doctorRepository.delete(doctor);
        authServiceClient.deleteCredential(authId);
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .authId(doctor.getAuthId())
                .name(doctor.getName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .speciality(doctor.getSpeciality())
                .licenseNumber(doctor.getLicenseNumber())
                .experience(doctor.getExperience())
                .approvalStatus(doctor.getApprovalStatus())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }
}

package com.hms.appointment_service.service;

import com.hms.appointment_service.dto.request.AppointmentRequest;
import com.hms.appointment_service.dto.request.AppointmentStatusRequest;
import com.hms.appointment_service.dto.response.AppointmentResponse;
import com.hms.appointment_service.entity.Appointment;
import com.hms.appointment_service.enums.AppointmentStatus;
import com.hms.appointment_service.exception.ResourceNotFoundException;
import com.hms.appointment_service.exception.UnauthorizedException;
import com.hms.appointment_service.feign.UserServiceClient;
import com.hms.appointment_service.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public AppointmentResponse bookAppointment(AppointmentRequest request, Long patientAuthId) {

        // Verify doctor exists and is approved
        UserServiceClient.DoctorResponse doctorResponse = userServiceClient.getDoctorByAuthId(request.getDoctorAuthId());
        if (!doctorResponse.isSuccess() || doctorResponse.getData() == null) {
            throw new ResourceNotFoundException("Doctor not found");
        }
        if (!"APPROVED".equals(doctorResponse.getData().getApprovalStatus())) {
            throw new UnauthorizedException("Doctor is not approved");
        }

        // Check double booking
        boolean slotTaken = appointmentRepository
                .existsByDoctorAuthIdAndAppointmentDateAndAppointmentTime(
                        request.getDoctorAuthId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime()
                );
        if (slotTaken) {
            throw new UnauthorizedException("This time slot is already booked");
        }

        Appointment appointment = Appointment.builder()
                .patientAuthId(patientAuthId)
                .doctorAuthId(request.getDoctorAuthId())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        return mapToResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getMyAppointments(Long authId, String role) {
        List<Appointment> appointments;

        if (role.equals("ROLE_PATIENT")) {
            appointments = appointmentRepository.findByPatientAuthId(authId);
        } else if (role.equals("ROLE_DOCTOR")) {
            appointments = appointmentRepository.findByDoctorAuthId(authId);
        } else {
            appointments = appointmentRepository.findAll();
        }

        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusRequest request, Long authId, String role) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Doctor can only update their own appointments
        if (role.equals("ROLE_DOCTOR") && !appointment.getDoctorAuthId().equals(authId)) {
            throw new UnauthorizedException("You can only update your own appointments");
        }

        appointment.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse cancelAppointment(Long id, Long authId, String role) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Patient can only cancel their own appointments
        if (role.equals("ROLE_PATIENT") && !appointment.getPatientAuthId().equals(authId)) {
            throw new UnauthorizedException("You can only cancel your own appointments");
        }

        // Can't cancel already completed or cancelled appointments
        if (appointment.getStatus() == AppointmentStatus.COMPLETED ||
                appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new UnauthorizedException("Cannot cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointmentRepository.delete(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        String patientName = "Unknown";
        String doctorName = "Unknown";

        try {
            UserServiceClient.PatientResponse patientResponse =
                    userServiceClient.getPatientByAuthId(appointment.getPatientAuthId());
            if (patientResponse.isSuccess() && patientResponse.getData() != null) {
                patientName = patientResponse.getData().getName();
            }
        } catch (Exception e) {
            // user-service might be down
        }

        try {
            UserServiceClient.DoctorResponse doctorResponse =
                    userServiceClient.getDoctorByAuthId(appointment.getDoctorAuthId());
            if (doctorResponse.isSuccess() && doctorResponse.getData() != null) {
                doctorName = doctorResponse.getData().getName();
            }
        } catch (Exception e) {
            // user-service might be down
        }

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientAuthId(appointment.getPatientAuthId())
                .patientName(patientName)
                .doctorAuthId(appointment.getDoctorAuthId())
                .doctorName(doctorName)
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
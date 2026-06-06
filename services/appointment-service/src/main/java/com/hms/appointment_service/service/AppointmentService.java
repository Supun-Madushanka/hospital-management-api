package com.hms.appointment_service.service;

import com.hms.appointment_service.dto.request.AppointmentRequest;
import com.hms.appointment_service.dto.request.AppointmentStatusRequest;
import com.hms.appointment_service.dto.response.AppointmentResponse;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentRequest request, Long patientAuthId);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponse> getMyAppointments(Long authId, String role);
    List<AppointmentResponse> getAllAppointments();
    AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusRequest request, Long authId, String role);
    AppointmentResponse cancelAppointment(Long id, Long authId, String role);
}
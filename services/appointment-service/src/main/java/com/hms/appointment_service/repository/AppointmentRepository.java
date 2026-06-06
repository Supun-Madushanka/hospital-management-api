package com.hms.appointment_service.repository;

import com.hms.appointment_service.entity.Appointment;
import com.hms.appointment_service.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientAuthId(Long patientAuthId);
    List<Appointment> findByDoctorAuthId(Long doctorAuthId);
    List<Appointment> findByStatus(AppointmentStatus status);
    boolean existsByDoctorAuthIdAndAppointmentDateAndAppointmentTime(
            Long doctorAuthId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );
}

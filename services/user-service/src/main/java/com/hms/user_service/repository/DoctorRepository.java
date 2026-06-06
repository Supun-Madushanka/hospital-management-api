package com.hms.user_service.repository;

import com.hms.user_service.entity.Doctor;
import com.hms.user_service.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByLicenseNumber(String licenseNumber);
    Optional<Doctor> findByAuthId(Long authId);
    List<Doctor> findByApprovalStatus(ApprovalStatus approvalStatus);
}

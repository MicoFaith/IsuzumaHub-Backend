package com.isuzumahub.diagnostic.repository;

import com.isuzumahub.diagnostic.model.TestResult;
import com.isuzumahub.diagnostic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Optional<TestResult> findByPatientEmail(String email);
    long countByPatient(User patient);
}
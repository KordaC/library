package com.example.library.repository;

import com.example.library.entity.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, UUID> {
}

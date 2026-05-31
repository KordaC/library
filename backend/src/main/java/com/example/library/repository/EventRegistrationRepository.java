package com.example.library.repository;

import com.example.library.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, UUID> {
    long countByEventId(UUID eventId);
    Optional<EventRegistration> findByEventIdAndUserId(UUID eventId, UUID userId);
    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);
}

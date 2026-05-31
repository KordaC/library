package com.example.library.repository;

import com.example.library.entity.ReaderProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReaderProfileRepository extends JpaRepository<ReaderProfile, UUID> {
}

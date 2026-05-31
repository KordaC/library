package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.ProfileDtos;
import com.example.library.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ApiResponse<ProfileDtos.ProfileResponse> get(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(profileService.getProfile(userId));
    }

    @PatchMapping
    public ApiResponse<ProfileDtos.ProfileResponse> update(
            Authentication authentication,
            @Valid @RequestBody ProfileDtos.UpdateProfileRequest request
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(profileService.updateProfile(userId, request));
    }
}

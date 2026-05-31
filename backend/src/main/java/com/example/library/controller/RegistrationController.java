package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.AuthDtos;
import com.example.library.dto.RegistrationDtos;
import com.example.library.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/verify-card")
    public ApiResponse<RegistrationDtos.VerifyCardResponse> verifyCard(
            @Valid @RequestBody RegistrationDtos.VerifyCardRequest request
    ) {
        return ApiResponse.ok(registrationService.verifyCard(request));
    }

    @PostMapping("/link-card")
    public ApiResponse<AuthDtos.LoginResponse> linkCard(
            @Valid @RequestBody RegistrationDtos.LinkCardRequest request
    ) {
        return ApiResponse.ok(registrationService.linkCard(request));
    }

    @PostMapping("/new")
    public ApiResponse<RegistrationDtos.NewRegistrationResponse> createNew(
            @Valid @RequestBody RegistrationDtos.NewRegistrationRequest request
    ) {
        return ApiResponse.ok(registrationService.createRequest(request));
    }

    @PostMapping("/new/{requestId}/mock-pay")
    public ApiResponse<RegistrationDtos.MockPayResponse> mockPay(@PathVariable UUID requestId) {
        return ApiResponse.ok(registrationService.mockPay(requestId));
    }

    @PostMapping("/new/{requestId}/complete")
    public ApiResponse<AuthDtos.LoginResponse> complete(
            @PathVariable UUID requestId,
            @Valid @RequestBody RegistrationDtos.CompleteRegistrationRequest body
    ) {
        return ApiResponse.ok(registrationService.completeRegistration(requestId, body));
    }

    @GetMapping("/new/{requestId}/status")
    public ApiResponse<RegistrationDtos.RegistrationStatusResponse> status(@PathVariable UUID requestId) {
        return ApiResponse.ok(registrationService.getStatus(requestId));
    }
}

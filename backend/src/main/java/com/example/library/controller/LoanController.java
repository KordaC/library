package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BookDtos;
import com.example.library.dto.LoanDtos;
import com.example.library.service.LoanService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/active")
    public ApiResponse<List<LoanDtos.LoanItem>> active(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(loanService.getActiveLoans(userId));
    }

    @GetMapping("/history")
    public ApiResponse<List<LoanDtos.LoanItem>> history(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(loanService.getHistory(userId));
    }

    @PostMapping("/{id}/renew")
    public ApiResponse<BookDtos.RenewResponse> renew(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(loanService.renewLoan(userId, id));
    }
}

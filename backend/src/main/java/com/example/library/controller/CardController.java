package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.CardDtos;
import com.example.library.service.CardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.library.web.PublicUrlResolver;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;
    private final PublicUrlResolver publicUrlResolver;

    public CardController(CardService cardService, PublicUrlResolver publicUrlResolver) {
        this.cardService = cardService;
        this.publicUrlResolver = publicUrlResolver;
    }

    @GetMapping("/me")
    public ApiResponse<CardDtos.CardResponse> myCard(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(cardService.getMyCard(userId));
    }

    @GetMapping("/me/qr")
    public ApiResponse<CardDtos.QrResponse> myQr(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(cardService.getQrForUser(userId, publicUrlResolver.resolveBaseUrl()));
    }
}

package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.CardDtos;
import com.example.library.exception.ApiException;
import com.example.library.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class PublicTicketController {

    private final CardService cardService;

    public PublicTicketController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/ticket")
    public ApiResponse<CardDtos.QrCardView> ticket(@RequestParam String token) {
        try {
            return ApiResponse.ok(cardService.resolveQrToken(token));
        } catch (IllegalArgumentException e) {
            throw new ApiException("INVALID_TOKEN", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

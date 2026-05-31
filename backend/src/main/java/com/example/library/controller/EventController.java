package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.EventDtos;
import com.example.library.service.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ApiResponse<List<EventDtos.EventItem>> list(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(eventService.listEvents(userId));
    }

    @PostMapping("/{id}/register")
    public ApiResponse<EventDtos.RegisterResponse> register(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.ok(eventService.register(userId, id));
    }

    @DeleteMapping("/{id}/register")
    public ApiResponse<Void> unregister(Authentication authentication, @PathVariable UUID id) {
        UUID userId = (UUID) authentication.getPrincipal();
        eventService.unregister(userId, id);
        return ApiResponse.ok(null);
    }
}

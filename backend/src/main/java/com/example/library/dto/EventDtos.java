package com.example.library.dto;

public final class EventDtos {

    private EventDtos() {}

    public record EventItem(
            String id,
            String title,
            String type,
            String description,
            String startsAt,
            int capacity,
            int registeredCount,
            boolean registeredByMe
    ) {}

    public record RegisterResponse(boolean registered) {}
}

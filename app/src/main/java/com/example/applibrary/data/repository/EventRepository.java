package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.EventDtos;

import java.util.List;

public class EventRepository {

    private final LibraryApi api;

    public EventRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<List<EventDtos.EventItem>> listEvents() {
        return ApiCallHandler.execute(api.listEvents());
    }

    public ApiResult<Object> register(String eventId) {
        return ApiCallHandler.execute(api.registerEvent(eventId));
    }

    public ApiResult<Void> unregister(String eventId) {
        return ApiCallHandler.executeVoid(api.unregisterEvent(eventId));
    }
}

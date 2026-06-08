package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.TicketDtos;

public class TicketRepository {

    private final LibraryApi api;

    public TicketRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<TicketDtos.QrCardView> resolveToken(String token) {
        return ApiCallHandler.execute(api.resolveTicket(token));
    }
}

package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.DashboardDtos;

public class DashboardRepository {

    private final LibraryApi api;

    public DashboardRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<DashboardDtos.DashboardResponse> loadDashboard() {
        return ApiCallHandler.execute(api.dashboard());
    }

    public ApiResult<DashboardDtos.QrResponse> loadQr() {
        return ApiCallHandler.execute(api.qr());
    }
}

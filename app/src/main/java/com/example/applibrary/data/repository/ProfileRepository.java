package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.ProfileDtos;

public class ProfileRepository {

    private final LibraryApi api;

    public ProfileRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<ProfileDtos.ProfileResponse> getProfile() {
        return ApiCallHandler.execute(api.getProfile());
    }

    public ApiResult<ProfileDtos.ProfileResponse> updateProfile(String email, String phone) {
        return ApiCallHandler.execute(api.updateProfile(new ProfileDtos.UpdateProfileRequest(email, phone)));
    }
}

package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.AuthDtos;
import com.example.applibrary.data.remote.dto.RegistrationDtos;

public class RegistrationRepository {

    private final LibraryApi api;

    public RegistrationRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<RegistrationDtos.VerifyCardResponse> verifyCard(String cardNumber) {
        return ApiCallHandler.execute(api.verifyCard(new RegistrationDtos.VerifyCardRequest(cardNumber)));
    }

    public ApiResult<AuthDtos.LoginResponse> linkCard(
            String cardNumber, String birthDate, String password, String passwordConfirm
    ) {
        return ApiCallHandler.execute(api.linkCard(
                new RegistrationDtos.LinkCardRequest(cardNumber, birthDate, password, passwordConfirm)));
    }

    public ApiResult<RegistrationDtos.NewRegistrationResponse> createRegistration(
            RegistrationDtos.NewRegistrationRequest request
    ) {
        return ApiCallHandler.execute(api.createRegistration(request));
    }

    public ApiResult<RegistrationDtos.MockPayResponse> mockPay(String requestId) {
        return ApiCallHandler.execute(api.mockPay(requestId));
    }

    public ApiResult<AuthDtos.LoginResponse> complete(String requestId, String password, String passwordConfirm) {
        return ApiCallHandler.execute(api.completeRegistration(
                requestId, new RegistrationDtos.CompleteRequest(password, passwordConfirm)));
    }
}

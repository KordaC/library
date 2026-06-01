package com.example.applibrary.data.remote;

import com.example.applibrary.data.remote.dto.AuthDtos;
import com.example.applibrary.data.remote.dto.CatalogDtos;
import com.example.applibrary.data.remote.dto.DashboardDtos;
import com.example.applibrary.data.remote.dto.EventDtos;
import com.example.applibrary.data.remote.dto.HealthDtos;
import com.example.applibrary.data.remote.dto.LoanDtos;
import com.example.applibrary.data.remote.dto.ProfileDtos;
import com.example.applibrary.data.remote.dto.RegistrationDtos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LibraryApi {

    @GET("health")
    Call<ApiResponse<HealthDtos.HealthResponse>> health();

    @POST("auth/login")
    Call<ApiResponse<AuthDtos.LoginResponse>> login(@Body AuthDtos.LoginRequest request);

    @POST("registration/verify-card")
    Call<ApiResponse<RegistrationDtos.VerifyCardResponse>> verifyCard(
            @Body RegistrationDtos.VerifyCardRequest request);

    @POST("registration/link-card")
    Call<ApiResponse<AuthDtos.LoginResponse>> linkCard(@Body RegistrationDtos.LinkCardRequest request);

    @POST("registration/new")
    Call<ApiResponse<RegistrationDtos.NewRegistrationResponse>> createRegistration(
            @Body RegistrationDtos.NewRegistrationRequest request);

    @POST("registration/new/{requestId}/mock-pay")
    Call<ApiResponse<RegistrationDtos.MockPayResponse>> mockPay(@Path("requestId") String requestId);

    @POST("registration/new/{requestId}/complete")
    Call<ApiResponse<AuthDtos.LoginResponse>> completeRegistration(
            @Path("requestId") String requestId,
            @Body RegistrationDtos.CompleteRequest request);

    @GET("dashboard")
    Call<ApiResponse<DashboardDtos.DashboardResponse>> dashboard();

    @GET("cards/me/qr")
    Call<ApiResponse<DashboardDtos.QrResponse>> qr();

    @GET("genres")
    Call<ApiResponse<List<CatalogDtos.GenreItem>>> genres();

    @GET("books")
    Call<ApiResponse<List<CatalogDtos.BookListItem>>> books(
            @Query("q") String q,
            @Query("genreId") String genreId,
            @Query("sort") String sort
    );

    @GET("books/{id}")
    Call<ApiResponse<CatalogDtos.BookDetail>> book(@Path("id") String id);

    @GET("loans/active")
    Call<ApiResponse<List<LoanDtos.LoanItem>>> activeLoans();

    @GET("loans/history")
    Call<ApiResponse<List<LoanDtos.LoanItem>>> loanHistory();

    @POST("loans/{id}/renew")
    Call<ApiResponse<LoanDtos.RenewResponse>> renewLoan(@Path("id") String id);

    @GET("profile")
    Call<ApiResponse<ProfileDtos.ProfileResponse>> getProfile();

    @PATCH("profile")
    Call<ApiResponse<ProfileDtos.ProfileResponse>> updateProfile(@Body ProfileDtos.UpdateProfileRequest request);

    @GET("events")
    Call<ApiResponse<List<EventDtos.EventItem>>> listEvents();

    @POST("events/{id}/register")
    Call<ApiResponse<Object>> registerEvent(@Path("id") String id);

    @DELETE("events/{id}/register")
    Call<ApiResponse<Void>> unregisterEvent(@Path("id") String id);
}

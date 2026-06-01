package com.example.applibrary.di;

import android.content.Context;

import com.example.applibrary.BuildConfig;
import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.repository.AuthRepository;
import com.example.applibrary.data.repository.CatalogRepository;
import com.example.applibrary.data.repository.DashboardRepository;
import com.example.applibrary.data.repository.EventRepository;
import com.example.applibrary.data.repository.HealthRepository;
import com.example.applibrary.data.repository.LoanRepository;
import com.example.applibrary.data.repository.ProfileRepository;
import com.example.applibrary.data.repository.RegistrationRepository;
import com.example.applibrary.util.ServerUrlStorage;
import com.example.applibrary.util.TokenStorage;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContainer {

    private final TokenStorage tokenStorage;
    private final ServerUrlStorage serverUrlStorage;
    private final LibraryApi api;
    private final AuthRepository authRepository;
    private final RegistrationRepository registrationRepository;
    private final DashboardRepository dashboardRepository;
    private final CatalogRepository catalogRepository;
    private final LoanRepository loanRepository;
    private final ProfileRepository profileRepository;
    private final EventRepository eventRepository;
    private final HealthRepository healthRepository;

    public AppContainer(Context context) {
        tokenStorage = new TokenStorage(context);
        serverUrlStorage = new ServerUrlStorage(context);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);

        // Render free tier: «пробуждение» 30–90 с после простоя
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    var request = chain.request();
                    var builder = request.newBuilder();
                    String token = tokenStorage.getAccessToken();
                    if (token != null && !token.isEmpty()) {
                        builder.addHeader("Authorization", "Bearer " + token);
                    }
                    if (request.url().host().contains("ngrok")) {
                        builder.addHeader("ngrok-skip-browser-warning", "true");
                    }
                    return chain.proceed(builder.build());
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrlStorage.getEffectiveBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(LibraryApi.class);
        authRepository = new AuthRepository(api, tokenStorage);
        registrationRepository = new RegistrationRepository(api);
        dashboardRepository = new DashboardRepository(api);
        catalogRepository = new CatalogRepository(api);
        loanRepository = new LoanRepository(api);
        profileRepository = new ProfileRepository(api);
        eventRepository = new EventRepository(api);
        healthRepository = new HealthRepository(api);
    }

    public HealthRepository getHealthRepository() {
        return healthRepository;
    }

    public TokenStorage getTokenStorage() {
        return tokenStorage;
    }

    public ServerUrlStorage getServerUrlStorage() {
        return serverUrlStorage;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public RegistrationRepository getRegistrationRepository() {
        return registrationRepository;
    }

    public DashboardRepository getDashboardRepository() {
        return dashboardRepository;
    }

    public CatalogRepository getCatalogRepository() {
        return catalogRepository;
    }

    public LoanRepository getLoanRepository() {
        return loanRepository;
    }

    public ProfileRepository getProfileRepository() {
        return profileRepository;
    }

    public EventRepository getEventRepository() {
        return eventRepository;
    }
}

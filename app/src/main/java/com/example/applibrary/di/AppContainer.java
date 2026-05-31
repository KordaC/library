package com.example.applibrary.di;

import android.content.Context;

import com.example.applibrary.BuildConfig;
import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.repository.AuthRepository;
import com.example.applibrary.data.repository.CatalogRepository;
import com.example.applibrary.data.repository.DashboardRepository;
import com.example.applibrary.data.repository.EventRepository;
import com.example.applibrary.data.repository.LoanRepository;
import com.example.applibrary.data.repository.ProfileRepository;
import com.example.applibrary.data.repository.RegistrationRepository;
import com.example.applibrary.util.TokenStorage;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContainer {

    private final TokenStorage tokenStorage;
    private final LibraryApi api;
    private final AuthRepository authRepository;
    private final RegistrationRepository registrationRepository;
    private final DashboardRepository dashboardRepository;
    private final CatalogRepository catalogRepository;
    private final LoanRepository loanRepository;
    private final ProfileRepository profileRepository;
    private final EventRepository eventRepository;

    public AppContainer(Context context) {
        tokenStorage = new TokenStorage(context);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    var request = chain.request();
                    String token = tokenStorage.getAccessToken();
                    if (token != null && !token.isEmpty()) {
                        request = request.newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                    }
                    return chain.proceed(request);
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
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
    }

    public TokenStorage getTokenStorage() {
        return tokenStorage;
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

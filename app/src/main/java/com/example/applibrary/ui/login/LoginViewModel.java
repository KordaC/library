package com.example.applibrary.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.applibrary.BuildConfig;
import com.example.applibrary.LibraryApplication;
import com.example.applibrary.data.remote.dto.AuthDtos;
import com.example.applibrary.data.repository.ApiResult;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<AuthDtos.LoginResponse> success = new MutableLiveData<>();
    private final MutableLiveData<String> status = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<AuthDtos.LoginResponse> getSuccess() {
        return success;
    }

    public LiveData<String> getStatus() {
        return status;
    }

    public String getServerUrl() {
        return BuildConfig.BASE_URL;
    }

    public void login(String cardNumber, String password) {
        loading.setValue(true);
        error.setValue(null);
        new Thread(() -> {
            var container = ((LibraryApplication) getApplication()).getAppContainer();
            if (isCloudServer()) {
                status.postValue("Подключение к библиотеке…");
                if (!container.getHealthRepository().waitUntilReady()) {
                    error.postValue("Сервер пока недоступен. Подождите минуту и нажмите «Войти» ещё раз.");
                    loading.postValue(false);
                    return;
                }
            }
            status.postValue("Вход…");
            var auth = container.getAuthRepository();
            ApiResult<AuthDtos.LoginResponse> result = auth.login(cardNumber, password);
            if (result instanceof ApiResult.Success) {
                success.postValue(((ApiResult.Success<AuthDtos.LoginResponse>) result).getData());
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<AuthDtos.LoginResponse>) result).getMessage());
            }
            status.postValue(null);
            loading.postValue(false);
        }).start();
    }

    private static boolean isCloudServer() {
        String url = BuildConfig.BASE_URL.toLowerCase();
        return url.startsWith("https://") && !url.contains("192.168.") && !url.contains("10.0.2.2");
    }
}

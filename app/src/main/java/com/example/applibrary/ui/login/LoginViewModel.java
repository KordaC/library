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
            if (BuildConfig.BASE_URL.contains("onrender")) {
                status.postValue("Подключение к серверу в облаке…");
                if (!container.getHealthRepository().ping()) {
                    error.postValue(
                            "Сервер Render ещё просыпается. Откройте в браузере адрес /health, "
                                    + "подождите 1–2 минуты и нажмите «Войти» снова.");
                    loading.postValue(false);
                    return;
                }
            }
            status.postValue(null);
            var auth = container.getAuthRepository();
            ApiResult<AuthDtos.LoginResponse> result = auth.login(cardNumber, password);
            if (result instanceof ApiResult.Success) {
                success.postValue(((ApiResult.Success<AuthDtos.LoginResponse>) result).getData());
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<AuthDtos.LoginResponse>) result).getMessage());
            }
            loading.postValue(false);
        }).start();
    }
}

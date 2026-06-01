package com.example.applibrary.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.data.remote.dto.AuthDtos;
import com.example.applibrary.data.repository.ApiResult;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<AuthDtos.LoginResponse> success = new MutableLiveData<>();

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

    public void login(String cardNumber, String password) {
        loading.setValue(true);
        error.setValue(null);
        new Thread(() -> {
            var auth = ((LibraryApplication) getApplication()).getAppContainer().getAuthRepository();
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

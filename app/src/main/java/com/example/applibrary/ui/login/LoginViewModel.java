package com.example.applibrary.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.applibrary.data.remote.dto.AuthDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.data.repository.AuthRepository;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<AuthDtos.LoginResponse> success = new MutableLiveData<>();

    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
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
            ApiResult<AuthDtos.LoginResponse> result = authRepository.login(cardNumber, password);
            if (result instanceof ApiResult.Success) {
                success.postValue(((ApiResult.Success<AuthDtos.LoginResponse>) result).getData());
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<AuthDtos.LoginResponse>) result).getMessage());
            }
            loading.postValue(false);
        }).start();
    }
}

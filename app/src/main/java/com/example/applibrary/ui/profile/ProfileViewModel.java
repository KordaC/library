package com.example.applibrary.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.applibrary.data.remote.dto.ProfileDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.data.repository.ProfileRepository;

public class ProfileViewModel extends ViewModel {

    private final ProfileRepository profileRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<ProfileDtos.ProfileResponse> profile = new MutableLiveData<>();

    public ProfileViewModel(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getMessage() { return message; }
    public LiveData<ProfileDtos.ProfileResponse> getProfile() { return profile; }

    public void load() {
        loading.setValue(true);
        new Thread(() -> {
            ApiResult<ProfileDtos.ProfileResponse> p = profileRepository.getProfile();
            if (p instanceof ApiResult.Success) {
                profile.postValue(((ApiResult.Success<ProfileDtos.ProfileResponse>) p).getData());
            } else if (p instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<ProfileDtos.ProfileResponse>) p).getMessage());
            }
            loading.postValue(false);
        }).start();
    }

    public void save(String email, String phone) {
        loading.setValue(true);
        new Thread(() -> {
            ApiResult<ProfileDtos.ProfileResponse> result = profileRepository.updateProfile(email, phone);
            if (result instanceof ApiResult.Success) {
                profile.postValue(((ApiResult.Success<ProfileDtos.ProfileResponse>) result).getData());
                message.postValue("saved");
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<ProfileDtos.ProfileResponse>) result).getMessage());
            }
            loading.postValue(false);
        }).start();
    }
}

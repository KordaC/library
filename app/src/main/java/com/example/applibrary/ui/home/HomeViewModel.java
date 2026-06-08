package com.example.applibrary.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.Application;

import com.example.applibrary.data.remote.dto.DashboardDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.data.repository.DashboardRepository;
import com.example.applibrary.util.QrScanUrlHelper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class HomeViewModel extends AndroidViewModel {

    private final DashboardRepository dashboardRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<DashboardDtos.DashboardResponse> dashboard = new MutableLiveData<>();
    private final MutableLiveData<String> qrScanUrl = new MutableLiveData<>();
    private final MutableLiveData<DashboardDtos.QrPayload> qrPayload = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application, DashboardRepository dashboardRepository) {
        super(application);
        this.dashboardRepository = dashboardRepository;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<DashboardDtos.DashboardResponse> getDashboard() {
        return dashboard;
    }

    public LiveData<String> getQrScanUrl() {
        return qrScanUrl;
    }

    public LiveData<DashboardDtos.QrPayload> getQrPayload() {
        return qrPayload;
    }

    public void load() {
        loading.postValue(true);
        error.postValue(null);
        new Thread(() -> {
            ApiResult<DashboardDtos.DashboardResponse> dashResult = dashboardRepository.loadDashboard();
            if (dashResult instanceof ApiResult.Success) {
                dashboard.postValue(((ApiResult.Success<DashboardDtos.DashboardResponse>) dashResult).getData());
            } else if (dashResult instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<DashboardDtos.DashboardResponse>) dashResult).getMessage());
            }

            ApiResult<DashboardDtos.QrResponse> qrResult = dashboardRepository.loadQr();
            if (qrResult instanceof ApiResult.Success) {
                DashboardDtos.QrResponse qr = ((ApiResult.Success<DashboardDtos.QrResponse>) qrResult).getData();
                if (qr.payload != null) {
                    qrPayload.postValue(qr.payload);
                }
                String url = QrScanUrlHelper.resolve(getApplication(), qr);
                if (url != null) {
                    qrScanUrl.postValue(url);
                }
            } else if (qrResult instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<DashboardDtos.QrResponse>) qrResult).getMessage());
            }
            loading.postValue(false);
        }).start();
    }
}

package com.example.applibrary.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.applibrary.data.remote.dto.DashboardDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.data.repository.DashboardRepository;

public class HomeViewModel extends ViewModel {

    private final DashboardRepository dashboardRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<DashboardDtos.DashboardResponse> dashboard = new MutableLiveData<>();
    private final MutableLiveData<DashboardDtos.QrPayload> qrPayload = new MutableLiveData<>();

    public HomeViewModel(DashboardRepository dashboardRepository) {
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

    public LiveData<DashboardDtos.QrPayload> getQrPayload() {
        return qrPayload;
    }

    public void load() {
        loading.setValue(true);
        error.setValue(null);
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
                if (qr != null && qr.payload != null) {
                    qrPayload.postValue(qr.payload);
                }
            }
            loading.postValue(false);
        }).start();
    }
}

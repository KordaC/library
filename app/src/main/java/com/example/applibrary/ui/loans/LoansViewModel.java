package com.example.applibrary.ui.loans;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.applibrary.data.remote.dto.LoanDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.data.repository.LoanRepository;

import java.util.ArrayList;
import java.util.List;

public class LoansViewModel extends ViewModel {

    private final LoanRepository loanRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<List<LoanDtos.LoanItem>> activeLoans = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<LoanDtos.LoanItem>> historyLoans = new MutableLiveData<>(new ArrayList<>());

    public LoansViewModel(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getMessage() { return message; }
    public LiveData<List<LoanDtos.LoanItem>> getActiveLoans() { return activeLoans; }
    public LiveData<List<LoanDtos.LoanItem>> getHistoryLoans() { return historyLoans; }

    public void loadAll() {
        loading.setValue(true);
        new Thread(() -> {
            ApiResult<List<LoanDtos.LoanItem>> active = loanRepository.loadActive();
            if (active instanceof ApiResult.Success) {
                activeLoans.postValue(((ApiResult.Success<List<LoanDtos.LoanItem>>) active).getData());
            } else if (active instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<List<LoanDtos.LoanItem>>) active).getMessage());
            }

            ApiResult<List<LoanDtos.LoanItem>> history = loanRepository.loadHistory();
            if (history instanceof ApiResult.Success) {
                historyLoans.postValue(((ApiResult.Success<List<LoanDtos.LoanItem>>) history).getData());
            }
            loading.postValue(false);
        }).start();
    }

    public void renew(String loanId) {
        new Thread(() -> {
            ApiResult<LoanDtos.RenewResponse> result = loanRepository.renew(loanId);
            if (result instanceof ApiResult.Success) {
                LoanDtos.RenewResponse data = ((ApiResult.Success<LoanDtos.RenewResponse>) result).getData();
                message.postValue("Срок продлён до " + data.newDueDate);
                loadAll();
            } else if (result instanceof ApiResult.Error) {
                error.postValue(((ApiResult.Error<LoanDtos.RenewResponse>) result).getMessage());
            }
        }).start();
    }
}

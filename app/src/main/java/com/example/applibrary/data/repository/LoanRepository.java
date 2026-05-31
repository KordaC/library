package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.LoanDtos;

import java.util.List;

public class LoanRepository {

    private final LibraryApi api;

    public LoanRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<List<LoanDtos.LoanItem>> loadActive() {
        return ApiCallHandler.execute(api.activeLoans());
    }

    public ApiResult<List<LoanDtos.LoanItem>> loadHistory() {
        return ApiCallHandler.execute(api.loanHistory());
    }

    public ApiResult<LoanDtos.RenewResponse> renew(String loanId) {
        return ApiCallHandler.execute(api.renewLoan(loanId));
    }
}

package com.example.applibrary.data.remote;

public class ApiErrorBody {
    public ErrorBody error;

    public static class ErrorBody {
        public String code;
        public String message;
    }
}

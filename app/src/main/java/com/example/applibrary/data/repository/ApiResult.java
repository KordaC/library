package com.example.applibrary.data.repository;

public abstract class ApiResult<T> {

    private ApiResult() {}

    public static <T> Success<T> success(T data) {
        return new Success<>(data);
    }

    public static <T> Error<T> error(String message) {
        return new Error<>(message);
    }

    public static final class Success<T> extends ApiResult<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static final class Error<T> extends ApiResult<T> {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

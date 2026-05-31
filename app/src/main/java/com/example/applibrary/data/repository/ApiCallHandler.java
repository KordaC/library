package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.ApiErrorBody;
import com.example.applibrary.data.remote.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public final class ApiCallHandler {

    private static final Gson GSON = new Gson();

    private ApiCallHandler() {}

    public static <T> ApiResult<T> execute(Call<ApiResponse<T>> call) {
        try {
            Response<ApiResponse<T>> response = call.execute();
            if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                return ApiResult.success(response.body().data);
            }
            return ApiResult.error(parseError(response.errorBody()));
        } catch (IOException e) {
            return ApiResult.error("Нет связи с сервером. Проверьте подключение к сети.");
        }
    }

    public static ApiResult<Void> executeVoid(Call<ApiResponse<Void>> call) {
        try {
            Response<ApiResponse<Void>> response = call.execute();
            if (response.isSuccessful()) {
                return ApiResult.success(null);
            }
            return ApiResult.error(parseError(response.errorBody()));
        } catch (IOException e) {
            return ApiResult.error("Нет связи с сервером. Проверьте подключение к сети.");
        }
    }

    private static String parseError(ResponseBody body) {
        if (body == null) {
            return "Ошибка сервера";
        }
        try {
            String raw = body.string();
            ApiErrorBody err = GSON.fromJson(raw, ApiErrorBody.class);
            if (err != null && err.error != null && err.error.message != null) {
                return err.error.message;
            }
        } catch (Exception ignored) {
        }
        return "Ошибка запроса";
    }
}

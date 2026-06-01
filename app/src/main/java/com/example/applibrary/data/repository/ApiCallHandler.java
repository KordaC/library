package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.ApiErrorBody;
import com.example.applibrary.data.remote.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
            return ApiResult.error(networkMessage(e));
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
            return ApiResult.error(networkMessage(e));
        }
    }

    private static String networkMessage(IOException e) {
        if (e instanceof SocketTimeoutException) {
            return "Сервер долго не отвечает. На Render первый запрос после простоя может занять 1–2 минуты — попробуйте ещё раз.";
        }
        if (e instanceof UnknownHostException) {
            return "Сервер не найден. Проверьте адрес backend в сборке APK (onrender.com).";
        }
        return "Нет связи с сервером. Проверьте интернет и что backend на Render в статусе Running.";
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

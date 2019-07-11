package com.sawsan.inventory.remote.service;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    private ApiUtils() {}

    // Change below url to whatever is ip of localhost
    public static final String BASE_URL = "http://192.168.1.105:3000/";

    public static ApiService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }
}
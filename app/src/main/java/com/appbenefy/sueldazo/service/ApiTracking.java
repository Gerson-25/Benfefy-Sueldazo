package com.appbenefy.sueldazo.service;

import com.appbenefy.sueldazo.entity.app.NavigationTracking;
import com.appbenefy.sueldazo.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiTracking {

    String BASE_URL = Constants.Companion.getBASE_URL_TRACKING();

    @POST("tracking")
    Call<Boolean> trackingUser(@Body NavigationTracking navigationTracking);
}

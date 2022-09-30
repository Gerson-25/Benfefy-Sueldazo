package com.syntepro.appbeneficiosbolivia.service;

import com.syntepro.appbeneficiosbolivia.entity.app.NavigationTracking;
import com.syntepro.appbeneficiosbolivia.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiTracking {

    String BASE_URL = Constants.Companion.getBASE_URL_TRACKING();

    @POST("tracking")
    Call<Boolean> trackingUser(@Body NavigationTracking navigationTracking);
}

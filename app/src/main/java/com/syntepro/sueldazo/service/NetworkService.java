package com.syntepro.sueldazo.service;

import com.syntepro.sueldazo.entity.firebase.Cupon;
import com.syntepro.sueldazo.entity.firebase.Cupon_Beneficio;
import com.syntepro.sueldazo.entity.service.CercaDeTi;
import com.syntepro.sueldazo.entity.service.NotificacionPushWS;
import com.syntepro.sueldazo.entity.service.PushProximidad;
import com.syntepro.sueldazo.entity.service.Sucursales;
import com.syntepro.sueldazo.entity.service.UnlinkLoyalty;
import com.syntepro.sueldazo.entity.service.VersionRequest;
import com.syntepro.sueldazo.entity.service.VipCoupon;
import com.syntepro.sueldazo.entity.service.VipCouponsRequest;
import com.syntepro.sueldazo.utils.Constants;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NetworkService {
    String BASE_URL = Constants.Companion.getBASE_URL_MICRO();

    // Aprovecha
    @GET("aprovecha/{pais}")
    Call<ArrayList<Cupon>> getAprovecha(@Path("pais") String pais);

    @POST("AprovechaVip/AprovechaVip")
    Call<ArrayList<VipCoupon>> getAprovechaVIP(@Body VipCouponsRequest vipCouponsRequest);

    // Lo Nuevo
    @GET("nuevoscupones/{pais}")
    Call<ArrayList<Cupon>> getNuevos(@Path("pais") String pais);

    @POST("NuevosCuponesVip/NuevosVip")
    Call<ArrayList<VipCoupon>> getNuevosVIP(@Body VipCouponsRequest vipCouponsRequest);

    // Mejores
    @GET("mejores/{pais}")
    Call<ArrayList<Cupon>> getMejores(@Path("pais") String pais);

    @POST("MejoresVip/MejoresVip")
    Call<ArrayList<VipCoupon>> getMejoresVIP(@Body VipCouponsRequest vipCouponsRequest);

    @GET("cercadeti/{pais}/{lat}/{lon}")
    Call<ArrayList<CercaDeTi>> getCerca(@Path("pais") String pais, @Path("lat") double lat, @Path("lon") double lon);

    @GET("scanner/{pais}/{id}")
    Call<ArrayList<Cupon_Beneficio>> getScanner(@Path("pais") String pais, @Path("id") String id);

    @GET("sucursalesCampana/{pais}/{comercio}/{lat}/{lon}/{soloUna}")
    Call<ArrayList<Sucursales>> getSucursales(@Path("pais") String pais, @Path("comercio") String comercio, @Path("lat") double lat, @Path("lon") double lon, @Path("soloUna") boolean soloUna);

    @POST("PushProximidad")
    Call<ArrayList<PushProximidad>> getPush(@Body NotificacionPushWS notificacionPushWS);

    @Headers("Content-Type: application/json")
    @POST("PlanesLealtad/desvincularUsuarioEnPlan")
    Call<ResponseBody> unlinkLoyaltyPlan(@Body UnlinkLoyalty unlinkLoyalty);

    // Version
    @POST("Security/GetParametersApp")
    Call<ResponseBody> getVersion(@Body VersionRequest versionRequest);
}

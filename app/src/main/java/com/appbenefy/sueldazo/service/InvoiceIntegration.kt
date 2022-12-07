package com.appbenefy.sueldazo.service

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.entity.app.NavigationCategory
import com.appbenefy.sueldazo.entity.app.NavigationCoupon
import com.appbenefy.sueldazo.entity.app.NavigationTracking
import com.appbenefy.sueldazo.room.database.RoomDataBase
import com.appbenefy.sueldazo.room.entity.NavigationCategoryUser
import com.appbenefy.sueldazo.room.entity.NavigationCouponUser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class InvoiceIntegration {

    private var navigationCategoryUser: List<NavigationCategoryUser>? = null
    private var navigationCouponUser: List<NavigationCouponUser>? = null
    private val categories = ArrayList<NavigationCategory>()
    private val coupons = ArrayList<NavigationCoupon>()
    private val roomDataBase = RoomDataBase.getRoomDatabase(AndroidApplication.applicationContext())

    fun sendData(currentUser: FirebaseUser) {
        val navigationTracking = NavigationTracking()
        navigationCategoryUser = roomDataBase.accessDao().category as List<NavigationCategoryUser>
        navigationCouponUser = roomDataBase.accessDao().coupon as List<NavigationCouponUser>
        roomDataBase.accessDao().dropCategory()
        roomDataBase.accessDao().dropCoupon()

        navigationCategoryUser?.let {
            for (ncu in it) { categories.add(NavigationCategory(ncu.idCategoria, ncu.fechaRegistro)) }
        }
        navigationCouponUser?.let {
            for (ncu in it) { coupons.add(NavigationCoupon(ncu.idCupon, ncu.fechaRegistro)) }
        }

        val cu = roomDataBase.accessDao().country
        navigationTracking.pais = cu.abr
        navigationTracking.idUsuario = currentUser.uid
        navigationTracking.dispositivo = "Android"
        navigationTracking.categorias = categories
        navigationTracking.cupones = coupons
        val gson = Gson()
        val json = gson.toJson(navigationTracking)
        Log.e("DATOS", json)
        if (categories.size > 0 || coupons.size > 0) saveTracking(navigationTracking)
    }

    private fun saveTracking(navigationTracking: NavigationTracking) {
        val call = retrofit.create(ApiTracking::class.java).trackingUser(navigationTracking)
        call.enqueue(object : Callback<Boolean?> {
            override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (!response.body()!!) {
                            for (ncu in navigationCategoryUser!!) { roomDataBase.accessDao().addNavigationCategoryUser(ncu) }
                            for (ncu in navigationCouponUser!!) { roomDataBase.accessDao().addNavigationCouponUser(ncu) }
                        } else {
                            categories.clear()
                            coupons.clear()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {}
        })
    }

    private val retrofit: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            return Retrofit.Builder()
                    .baseUrl(ApiTracking.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }
}
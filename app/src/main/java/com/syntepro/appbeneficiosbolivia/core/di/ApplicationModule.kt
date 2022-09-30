/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.syntepro.appbeneficiosbolivia.core.di

import android.content.Context
import android.os.Build
import com.google.gson.GsonBuilder
import com.syntepro.appbeneficiosbolivia.accounts.model.AccountsRepository
import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyRepository
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceRepository
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponRepository
import com.syntepro.appbeneficiosbolivia.ui.home.model.HomeRepository
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanRepository
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationRepository
import com.syntepro.appbeneficiosbolivia.ui.profile.model.ProfileRepository
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ShopRepository
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SudamericanaRepository
import com.syntepro.appbeneficiosbolivia.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.CertificateException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
class ApplicationModule(private val application: AndroidApplication) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val m_client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        return Retrofit.Builder()
                .baseUrl("${Constants.BASE_URL_MICRO2}api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.addCallAdapterFactory(CoroutineCallAdapterFactory())
//                .client(createClient())
            .client(getOkHttpClient().build())
                .build()


    }

    /**
     * Gerson Aquino 10DIC2021
     *
     *new retrofit client to allow connection on devices
     * with api 24 or lower
     *
     */

    private fun getOkHttpClient(): OkHttpClient.Builder {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    var request = chain.request()
                    if (chain.request().header("No-Authentication") == null) {
                        request = request.newBuilder().addHeader(
                            "Authorization",
                            "Bearer ${Constants.TOKEN}"
                        ).build()
                    }
                    chain.proceed(request)
                }
                .apply {
                    this.addInterceptor(interceptor)
                }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
                builder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
                builder.hostnameVerifier ( hostnameVerifier = { _, _ -> true })
            }

            return builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun createClient(): OkHttpClient {

        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        //if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            //okHttpClientBuilder.addInterceptor(loggingInterceptor)
            okHttpClientBuilder.addInterceptor { chain ->
                var request = chain.request()
                if (chain.request().header("No-Authentication") == null) {
                    request = request.newBuilder().addHeader(
                            "Authorization",
                            "Bearer ${Constants.TOKEN}"
                    ).build()
                }
                chain.proceed(request)
            }.apply {
                this.addInterceptor(loggingInterceptor)
            }
       // }
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideAccountsRepository(dataSource: AccountsRepository.Network): AccountsRepository = dataSource

    @Provides
    @Singleton
    fun provideLoyaltyPlanRepository(dataSource: LoyaltyPlanRepository.Network): LoyaltyPlanRepository = dataSource

    @Provides
    @Singleton
    fun provideHomeRepository(dataSource: HomeRepository.Network): HomeRepository = dataSource

    @Provides
    @Singleton
    fun provideCommerceRepository(dataSource: CommerceRepository.Network): CommerceRepository = dataSource

    @Provides
    @Singleton
    fun provideCouponRepository(dataSource: CouponRepository.Network): CouponRepository = dataSource

    @Provides
    @Singleton
    fun provideNotificationRepository(dataSource: NotificationRepository.Network): NotificationRepository = dataSource

    @Provides
    @Singleton
    fun provideSudamericanaRepository(dataSource: SudamericanaRepository.Network): SudamericanaRepository = dataSource

    @Provides
    @Singleton
    fun provideProfileRepository(dataSource: ProfileRepository.Network): ProfileRepository = dataSource

    @Provides
    @Singleton
    fun provideAgencyRepository(dataSource: AgencyRepository.Network): AgencyRepository = dataSource

    @Provides
    @Singleton
    fun provideShopRepository(dataSource: ShopRepository.Network): ShopRepository = dataSource

}

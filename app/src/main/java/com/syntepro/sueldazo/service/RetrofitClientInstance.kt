package com.syntepro.sueldazo.service


import android.os.Build
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.syntepro.sueldazo.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.lang.Exception
import java.lang.RuntimeException
import java.security.*
import java.security.cert.CertificateException
import javax.net.ssl.*


object RetrofitClientInstance {
    private var retrofit: Retrofit? = null

    fun getClient(BASE_URL: String): Retrofit {

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        retrofit?.let { if (!it.baseUrl().equals(BASE_URL)) retrofit = null }
        when (retrofit) {
            null -> {
                val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }

                val client = OkHttpClient.Builder()
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
                        .build()

                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(CoroutineCallAdapterFactory())
                        .client(getOkHttpClient().build())
                        .build()
            }
        }
        return retrofit as Retrofit
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
            // Install the all-trusting trust manager
            val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }
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

}
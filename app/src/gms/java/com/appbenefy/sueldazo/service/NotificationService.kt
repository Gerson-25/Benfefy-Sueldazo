package com.appbenefy.sueldazo.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.entity.service.NotificacionPushWS
import com.appbenefy.sueldazo.entity.service.PushProximidad
import com.appbenefy.sueldazo.room.database.RoomDataBase
import com.appbenefy.sueldazo.room.entity.LocationServiceCounter
import com.appbenefy.sueldazo.room.entity.NotificationUser
import com.appbenefy.sueldazo.ui.explore.CercaDeTiActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class NotificationService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}
package com.syntepro.sueldazo.service

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
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.core.AndroidApplication
import com.syntepro.sueldazo.entity.service.NotificacionPushWS
import com.syntepro.sueldazo.entity.service.PushProximidad
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.room.entity.LocationServiceCounter
import com.syntepro.sueldazo.room.entity.NotificationUser
import com.syntepro.sueldazo.ui.explore.CercaDeTiActivity
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

    private val locationRequest by lazy { LocationRequest.create() }
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var shop: ArrayList<String>? = null

    override fun onBind(intent: Intent): IBinder? { return null }

    @SuppressLint("SimpleDateFormat")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        roomDataBase = RoomDataBase.getRoomDatabase(AndroidApplication.applicationContext())
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = MIN_TIME_LOCATION_CHANGE
        locationRequest.fastestInterval = MIN_DISTANCE_LOCATION_CHANCE
        locationRequest.smallestDisplacement = DISTANCE_TO_PLACE.toFloat()
        location
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        roomDataBase = RoomDataBase.getRoomDatabase(applicationContext)
        if (Build.VERSION.SDK_INT >= 26) {
            val channelID = "my_channel_01"
            val channel = NotificationChannel(channelID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH)
            (Objects.requireNonNull(getSystemService(NOTIFICATION_SERVICE)) as NotificationManager).createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, channelID)
                    .setContentTitle("")
                    .setContentText("").build()
            startForeground(1, notification)
        }
        shop = ArrayList()
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    @get:SuppressLint("MissingPermission")
    val location: Unit
        get() {
            fusedLocationProviderClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
        }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lon = locationResult.locations[0].longitude
            val lat = locationResult.locations[0].latitude
            val spd = locationResult.locations[0].speed.toDouble()
            val userConfiguration = roomDataBase!!.accessDao().configuration
            val numMaxNoti = userConfiguration.numNotificaciones

            val minSpeed = 15

            // Add counter notification WS
            val lsc = roomDataBase!!.accessDao().locationServiceCounter
            if (lsc != null) {
                val a = lsc.locationServiceCounter + 1
                val b = lsc.notificationWSCounter + 1
                roomDataBase!!.accessDao().updateLocationServiceCounter(a, b, 1)
            } else {
                val serviceCounter = LocationServiceCounter()
                serviceCounter.id = 1
                serviceCounter.locationServiceCounter = 1
                serviceCounter.notificationWSCounter = 0
                roomDataBase!!.accessDao().addLocationServiceCounter(serviceCounter)
            }
            if (userConfiguration.isRuta) {
                if (spd < minSpeed) {
                    val ids = TimeZone.getAvailableIDs()
                    val tz = TimeZone.getTimeZone(ids[144])
                    val c = Calendar.getInstance(tz)
                    val notification = roomDataBase!!.accessDao().notification
                    if (notification == null) {
                        val notificationUser = NotificationUser()
                        notificationUser.id = 1
                        notificationUser.fechaNotificacion = c.time
                        notificationUser.notificacion = 1
                        notificationUser.longitud = lon
                        notificationUser.latitud = lat
                        roomDataBase!!.accessDao().addNotificationUser(notificationUser)
                        getPush(lat, lon, shop)
                    } else {
                        @SuppressLint("SimpleDateFormat") val fmt = SimpleDateFormat("yyyyMMdd")
                        if (fmt.format(Objects.requireNonNull(notification).fechaNotificacion) == fmt.format(c.time)) {
                            if (notification.notificacion < numMaxNoti) {
                                val diff = c.time.time - notification.fechaNotificacion.time
                                val segMilli = 1000
                                val minMilli = segMilli * 60
                                val minutes = diff.toInt() / minMilli
                                if (minutes > 5) {
                                    val locationA = Location("Actual")
                                    locationA.latitude = lat
                                    locationA.longitude = lon
                                    val locationB = Location("Anterior")
                                    locationB.latitude = notification.latitud
                                    locationB.longitude = notification.longitud
                                    val distance = locationA.distanceTo(locationB)

                                    //5 Metros
                                    val MIN_DISTANCE_NOTIFICATION_CHANCE: Long = 5
                                    if (distance >= MIN_DISTANCE_NOTIFICATION_CHANCE) {
                                        val notificationUser = NotificationUser()
                                        notificationUser.id = 1
                                        notificationUser.fechaNotificacion = c.time
                                        notificationUser.notificacion = notification.notificacion + 1
                                        notificationUser.longitud = lon
                                        notificationUser.latitud = lat
                                        roomDataBase!!.accessDao().updateNotificationUser(notificationUser)
                                        getPush(lat, lon, shop)
                                    } else getPush(lat, lon, shop)
                                }
                            }
                        } else {
                            roomDataBase!!.accessDao().dropNotifications()
                            val notificationUser = NotificationUser()
                            notificationUser.id = 1
                            notificationUser.fechaNotificacion = c.time
                            notificationUser.notificacion = 1
                            notificationUser.longitud = lon
                            notificationUser.latitud = lat
                            roomDataBase!!.accessDao().addNotificationUser(notificationUser)
                            getPush(lat, lon, shop)
                        }
                    }
                }
            }
            super.onLocationResult(locationResult)
        }
    }

    private fun getPush(la: Double, lo: Double, comercios: ArrayList<String>?) {
        val cu = roomDataBase!!.accessDao().country
        val lsc = roomDataBase!!.accessDao().locationServiceCounter

        // Add counter notification WS
        if (lsc != null) {
            val a = lsc.locationServiceCounter + 1
            val b = lsc.notificationWSCounter + 1
            roomDataBase!!.accessDao().updateLocationServiceCounter(a, b, 1)
        } else {
            val serviceCounter = LocationServiceCounter()
            serviceCounter.id = 1
            serviceCounter.locationServiceCounter = 0
            serviceCounter.notificationWSCounter = 1
            roomDataBase!!.accessDao().addLocationServiceCounter(serviceCounter)
        }
        val notificacionPushWS = NotificacionPushWS()
        notificacionPushWS.pais = cu.abr
        notificacionPushWS.lat = la
        notificacionPushWS.lon = lo
        notificacionPushWS.distancia = 100.0
        notificacionPushWS.comerciosExcluidos = comercios

        val call = retrofit.create(NetworkService::class.java).getPush(notificacionPushWS)
        call.enqueue(object : Callback<ArrayList<PushProximidad>> {
            override fun onResponse(call: Call<ArrayList<PushProximidad>>, response: Response<ArrayList<PushProximidad>>) {
                if (response.code() == 200) {
                    val ap = response.body()!!
                    if (ap.isNotEmpty()) {
                        for (pp in ap) {
                            val title: String = if (Objects.requireNonNull(pp.notificacionPushTitulo) == "") "Aprovecha tus promociones"
                            else pp.notificacionPushTitulo

                            val message: String = if (Objects.requireNonNull(pp.notificacionPushMensaje) == "") "Ven y disfruta de las mejores promociones solo en " + pp.nombreComercio + " de " + pp.nombreSucursal + ", no te quedes sin tu promoción."
                            else pp.notificacionPushMensaje

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationCallO(title, message)
                            else notificationCall(title, message)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<PushProximidad>>, t: Throwable) {
                Log.i("Notification", "Server Error")
            }
        })
    }

    // Notification for API 26 or higher
    private fun notificationCallO(titulo: String, mensaje: String) {
        val resultIntent = Intent(this, CercaDeTiActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, "channel1")
                .setSmallIcon(R.drawable.ic_benefy)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText(mensaje))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .build()
        notificationManager!!.notify(Random().nextInt(), notification)
    }

    // Notification for API 25 or less
    private fun notificationCall(titulo: String, mensaje: String) {
        val resultIntent = Intent(this, CercaDeTiActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_benefy)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_benefy))
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Objects.requireNonNull(notificationManager).notify(Random().nextInt(), builder.build())
    }

    private val retrofit: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            return Retrofit.Builder()
                    .baseUrl(NetworkService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }

    companion object {
        private var roomDataBase: RoomDataBase? = null
        const val MIN_TIME_LOCATION_CHANGE = (2 * 60 * 1000).toLong() // Seconds (5 minutes)
        const val MIN_DISTANCE_LOCATION_CHANCE: Long = 50 // 500 Meters
        const val DISTANCE_TO_PLACE = 1000 // 1000 Meters
    }
}
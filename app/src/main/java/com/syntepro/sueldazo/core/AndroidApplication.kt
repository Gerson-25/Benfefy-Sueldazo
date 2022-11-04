package com.syntepro.sueldazo.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.syntepro.sueldazo.core.di.ApplicationComponent
import com.syntepro.sueldazo.core.di.ApplicationModule
import com.syntepro.sueldazo.core.di.DaggerApplicationComponent
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.room.entity.UserConfiguration
import java.util.*

class AndroidApplication : Application(), LifecycleObserver {

    init { instance = this }

    companion object {
        private var instance: AndroidApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    private var roomDataBase: RoomDataBase? = null

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) { DaggerApplicationComponent
            .builder()
            .applicationModule(
                    ApplicationModule(
                            this
                    )
            )
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        // ROOM Instance
        roomDataBase = RoomDataBase.getRoomDatabase(applicationContext)
        createNotificationChannel()
        configurationUser()

        this.injectMembers()
    }

    override fun onTerminate() {
        RoomDataBase.destroyInstance()
        super.onTerminate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        //App in background
        Log.e("LifeCycle", "************* backgrounded")
        Log.e("LifeCycle", "************* ${isInForeground()}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // App in foreground
        Log.e("LifeCycle", "************* foregrounded")
        Log.e("LifeCycle", "************* ${isInForeground()}")
    }

    private fun injectMembers() = appComponent.inject(this)

    fun isInForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_1_ID = "channel1"
            val channel1 = NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channel1.description = "This is channel 1"
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel1)
        }
    }

    private fun configurationUser() {
        val configuration = roomDataBase!!.accessDao().configuration
        if (configuration == null) {
            val ids = TimeZone.getAvailableIDs()
            val tz = TimeZone.getTimeZone(ids[144])
            val c = Calendar.getInstance(tz)
            val userConfiguration = UserConfiguration()
            userConfiguration.id = 1
            userConfiguration.fechaModificacion = c.time
            userConfiguration.idioma = "es"
            userConfiguration.isNuevoCupon = true
            userConfiguration.isRuta = true
            userConfiguration.isProximos = true
            userConfiguration.numNotificaciones = 5
            roomDataBase!!.accessDao().addUserConfiguration(userConfiguration)
        }
    }

}
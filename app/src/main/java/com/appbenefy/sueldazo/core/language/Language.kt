package com.appbenefy.sueldazo.core.language

import android.content.Context
import android.content.res.Configuration
import com.appbenefy.sueldazo.core.preference.AppPreference
import com.appbenefy.sueldazo.utils.Global
import java.util.*

object Language {
    const val LAN_ENGLISH = "en"
    const val LAN_SPANISH = "es"

    fun setLanguage(context: Context) {
        val defLanguage = AppPreference(context).getLanguage()
        val locale = Locale(defLanguage!!)
        Global.defLocale = locale
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)

        //val config = Configuration()
        //config.locale = locale
        //resources.updateConfiguration(config, resources.displayMetrics)


    }
}
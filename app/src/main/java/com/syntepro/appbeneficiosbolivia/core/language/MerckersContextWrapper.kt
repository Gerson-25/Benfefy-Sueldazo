package com.merckers.asesorcajero.core.language

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import com.syntepro.appbeneficiosbolivia.core.preference.AppPreference
import com.syntepro.appbeneficiosbolivia.utils.Global
import java.util.*

class MerckersContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        @SuppressWarnings("deprecation")
        fun wrap( context: Context, language: String?): ContextWrapper {
            var context = context
            val config = context.resources.configuration
            var lng = language
            var sysLocale: Locale? = null
            if(Global.defLocale == null) {
                Global.defLocale = Locale(AppPreference(context).getLanguage())
                lng = Global.defLocale?.language
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sysLocale = getSystemLocale(config)
            } else {
                sysLocale = getSystemLocaleLegacy(config)
            }

            if (lng != "" && sysLocale.language != lng) {
                val locale = Locale(lng!!)
                Global.defLocale = locale
                Locale.setDefault(locale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setSystemLocale(config, locale)
                } else {
                    setSystemLocaleLegacy(config, locale)
                }
            }
            context = context.createConfigurationContext(config)
            return MerckersContextWrapper(context)
        }

        fun getSystemLocaleLegacy(config: Configuration): Locale {
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun getSystemLocale(config: Configuration): Locale {
            return config.locales.get(0)
        }

        fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }
    }
}
package com.appbenefy.sueldazo.utils

import android.content.Context
import com.appbenefy.sueldazo.core.preference.AppPreference
import java.util.*

object Global {
    var defLocale: Locale? = null

    private var _defaultTheme: Int? = null
    fun getDefaultTheme(context: Context): Int {
        if(_defaultTheme == null)
            _defaultTheme = AppPreference(context).getDefaultTheme()!!
        return _defaultTheme!!
    }
}
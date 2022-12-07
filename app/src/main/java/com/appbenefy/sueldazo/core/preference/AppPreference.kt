package com.appbenefy.sueldazo.core.preference

import android.content.Context
import com.appbenefy.sueldazo.core.app.Version
import com.merckers.core.preference.Preference
import com.appbenefy.sueldazo.R
import java.text.SimpleDateFormat
import java.util.*

class AppPreference (val context: Context) {
    fun getVersion(): String = Preference(context).get(PREF_VERSION_CODE_KEY, DOESNT_EXIST)!!
    fun setVersion() = Preference(context).put(PREF_VERSION_CODE_KEY, Version.getVersion(context))
    fun getLanguage(): String = Preference(context).get(PREF_DEFAULT_LANGUAGE, Locale.getDefault().language)!!
    fun setLanguage(language: String) = Preference(context).put(PREF_DEFAULT_LANGUAGE, language)
//    fun setProfile(profile: Profile) = Preference(context).put(PREF_PROFILE, profile)
//    fun getProfile(): Profile? = Preference(context).get<Profile>(PREF_PROFILE, null)
    fun getDefaultTheme(): Int? = Preference(context).get(PREF_DEFAULT_THEME, R.style.AppTheme_Small)!!
    fun setToken(token: String) = Preference(context).put(PREF_TOKEN, token)
    fun getToken(): String = Preference(context).get(PREF_TOKEN, "")?:""

    fun setLastLogin(){
        val formatter = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault())
        val formattedDate = formatter.format( Date())
        Preference(context).put(PREF_LAST_LOGIN, formattedDate)
    }

    fun getLastLogin(): Date? {
        val dateStr = Preference(context).get(PREF_LAST_LOGIN, "")
        val formatter = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault())
        return if(dateStr.isNullOrEmpty()) null else formatter.parse(dateStr)
    }

    fun deleteLastLogin() {
        Preference(context).remove(PREF_LAST_LOGIN)
    }


    companion object {
        private const val DATETIME_FORMAT = "yyyyMMdd'T'HH:mm:ss"
        private const val PREF_VERSION_CODE_KEY = "appVersionCode"
        const val PREF_DEFAULT_LANGUAGE = "defaultLanguage"
        const val PREF_LAST_LOGIN = "lastLogin"
        const val PREF_PROFILE = "profile"
        const val PREF_DEFAULT_THEME = "defaultTheme"
        const val PREF_TOKEN = "token"
        const val DOESNT_EXIST = "-1"

    }
}
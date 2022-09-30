package com.merckers.core.preference

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()
    when (T::class) {
        String::class -> editor.putString(key, value as String)
        Int::class -> editor.putInt(key, value as Int)
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Long::class -> editor.putLong(key, value as Long)
        Float::class -> editor.putFloat(key, value as Float)
        else -> if (value is Set<*>) {
            editor.putStringSet(key, value as Set<String>)
        } else {  // Object
            val json = Gson().toJson(value)
            editor.putString(key, json)
        }
    }
    editor.commit()
}

inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T?): T? {
    when (T::class) {
        String::class -> return this.getString(key, defaultValue as String) as T
        Int::class -> return this.getInt(key, defaultValue as Int) as T
        Boolean::class -> return this.getBoolean(key, defaultValue as Boolean) as T
        Long::class -> return this.getLong(key, defaultValue as Long) as T
        Float::class -> return this.getFloat(key, defaultValue as Float) as T
        else -> if (defaultValue is Set<*>) {
            return this.getStringSet(key, defaultValue as Set<String>) as T
        } else { // Object
            val gson = Gson()

            val str = this.getString(key, if(defaultValue == null) null else defaultValue as String)
            if (!str.isNullOrEmpty())
                return gson.fromJson<T>(str, T::class.java)
        }
    }
    return defaultValue
}

class Preference(private val context: Context){
    private val PREF_FILE_NAME = "app.preferences"
    val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    inline fun <reified T> get(key: String, defaultValue: T?): T? {
        return prefs.get(key, defaultValue)
    }

    inline fun <reified  T> put(key: String, value: T) {
        prefs.put(key, value)
    }

    fun remove(key: String) = prefs.edit().remove(key).apply()
}




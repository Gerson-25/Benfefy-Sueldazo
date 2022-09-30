package com.syntepro.appbeneficiosbolivia.core.firebase

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FirebaseApi {
    const val PROFILE_COLLECTION = "profiles"
    const val PROFILE_TOKEN_COLLECTION = "deviceTokens"
    const val PROFILE_NOTIFICATIONS = "messages"
    const val SHUTDOWN_COLLECTION = "shutdown"
    const val PROFILE_IMAGES_PATH = "profileImages"
    const val PROFILE_THUMBS_IMAGE_PATH = "profileThumbImages"
    const val SHUTDOWN_DOCUMENT_ID = "1"

    fun initFirebase() {
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .setSslEnabled(true)
                .build()
            FirebaseFirestore.getInstance()
                .firestoreSettings = settings

            FirebaseFirestore.getInstance().clearPersistence()
//            if (BuildConfig.DEBUG) FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)

            FirebaseDatabase.getInstance().setPersistenceEnabled(false)
        } catch (e: java.lang.Exception) {
            Log.e("initFirebase", e.message!!)
        }
    }
}
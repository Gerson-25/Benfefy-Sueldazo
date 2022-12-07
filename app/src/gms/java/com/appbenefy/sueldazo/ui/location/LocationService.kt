package com.appbenefy.sueldazo.ui.location

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient

object LocationService {
    fun getFusedLocation(ctx: Context, onResult: (Double?, Double?) -> Unit) {
        FusedLocationProviderClient(ctx).lastLocation
            .addOnSuccessListener { onResult(it.longitude, it.latitude) }
            .addOnFailureListener { onResult(null, null) }
    }
}
package com.syntepro.appbeneficiosbolivia.ui.location

import android.content.Context
import com.huawei.hms.location.FusedLocationProviderClient

object LocationService {
    fun getFusedLocation(ctx: Context, onResult: (Double?, Double?) -> Unit) {
        FusedLocationProviderClient(ctx).lastLocation
            .addOnSuccessListener { onResult(it.longitude, it.latitude) }
            .addOnFailureListener { onResult(null, null) }
    }
}
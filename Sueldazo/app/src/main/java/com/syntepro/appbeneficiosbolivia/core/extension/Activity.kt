package com.syntepro.appbeneficiosbolivia.core.extension

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.ArrayList

private fun hasPermission(
    context: Context,
    permission: String?
): Boolean {
    return context.checkCallingOrSelfPermission(permission!!) ==
            PackageManager.PERMISSION_GRANTED
}

@RequiresApi(api = Build.VERSION_CODES.M)
fun Activity.requestPermission(permission: String, id: Int) {
    if (hasPermission(this, permission))
        return
    else {
        val permissionsToRequest = ArrayList<String>()
        permissionsToRequest.add(permission)
        this.requestPermissions(permissionsToRequest.toTypedArray(), id)
    }
}
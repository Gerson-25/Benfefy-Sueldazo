package syntepro.util.picker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Vibrator

object VibrationHelper {
    fun vibrate(context: Context, duration: Int) {
        if (hasVibrationPermission(context)) {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(duration.toLong())
        }
    }

    fun hasVibrationPermission(context: Context): Boolean {
        return context.packageManager.checkPermission(Manifest.permission.VIBRATE,
                context.packageName) == PackageManager.PERMISSION_GRANTED
    }
}

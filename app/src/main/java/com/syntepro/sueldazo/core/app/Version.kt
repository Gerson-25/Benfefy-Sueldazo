package com.syntepro.sueldazo.core.app

import android.content.Context

object Version {
    fun getVersion(ctx: Context): String =
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName

}
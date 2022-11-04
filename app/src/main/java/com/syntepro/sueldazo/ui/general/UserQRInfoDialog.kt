package com.syntepro.sueldazo.ui.general

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.entity.app.UserQRInformation
import com.syntepro.sueldazo.utils.Constants
import kotlinx.android.synthetic.main.user_qr_info_dialog.*

class UserQRInfoDialog : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.user_qr_info_dialog)
        this.setFinishOnTouchOutside(true)

        val data = UserQRInformation("${Constants.userProfile?.names} ${Constants.userProfile?.lastNames}", Constants.userProfile?.idUser ?: "")
        val jsonObject = Gson().toJson(data)

        val bitmap = bitmapQR(jsonObject)
        if (bitmap != null) uidQR.setImageBitmap(bitmap)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val view = window.decorView
        val lp = view.layoutParams as WindowManager.LayoutParams
        lp.gravity = Gravity.START or Gravity.CENTER
        lp.x = 0
        lp.y = 0
        lp.horizontalMargin = 0f
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        windowManager.updateViewLayout(view, lp)
    }

    private fun bitmapQR(id: String?) : Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        return try {
            if (id != null) {
                val bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 300, 300)
                val barcodeEncoder = BarcodeEncoder()
                barcodeEncoder.createBitmap(bitMatrix)
            } else null
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

}
package com.syntepro.sueldazo.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.entity.firebase.TarjetaSellos

class StampCardInfoDialog: BaseActivity() {

    private var canceled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        appComponent.inject(this)
        setContentView(R.layout.stamp_card_info_layout)
        this.setFinishOnTouchOutside(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val mPlan = extras.getString("planId")
            val mStampId = extras.getString("stampId")
        }

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

    override fun onBackPressed() {
        val data = Intent()
        if(!canceled)
            data.putExtra("refresh", true)
        setResult(RESULT_OK, data)
        super.onBackPressed()
        finish()
    }


    private fun getStatus(status: Int): String {
        var st = ""
        var color = "black"
        when(status) {
            TarjetaSellos.STATUS_INPROGRESS -> {
                st =  getString(R.string.stamp_status_inprogress)
            }
            TarjetaSellos.STATUS_COMPLETED -> {
                st =  getString(R.string.stamp_status_completed)
                color = "#02B256"
            }
            TarjetaSellos.STATUS_EXPIRED -> {
                st =  getString(R.string.stamp_status_expired)
                color = "#f76B1C"
            }
            TarjetaSellos.STATUS_REDEEMED -> {
                st =  getString(R.string.stamp_status_redimeed)
                color = "#4C84FF"
            }
        }
        return String.format(getString(R.string.stamp_status, color, st))
    }

}
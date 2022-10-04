package com.syntepro.appbeneficiosbolivia.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.main.stamp_info_layout.*
import java.text.DateFormat

class StampInfoDialog : BaseActivity() {

    private var canceled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.stamp_info_layout)
        this.setFinishOnTouchOutside(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {

        }

        closeId.setOnClickListener{
            canceled = true
            onBackPressed()
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

}
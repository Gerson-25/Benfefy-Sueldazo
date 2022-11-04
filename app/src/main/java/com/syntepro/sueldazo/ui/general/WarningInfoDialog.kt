package com.syntepro.sueldazo.ui.general

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.R
import kotlinx.android.synthetic.main.warning_layout.*

class WarningInfoDialog : BaseActivity() {

    private lateinit var warning: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.warning_layout)
        this.setFinishOnTouchOutside(true)

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            warning = extras.getString("warning")!!
        }

        warningId.text = warning
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

}
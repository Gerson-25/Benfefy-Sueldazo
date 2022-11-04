package com.syntepro.sueldazo.ui.general

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.R
import kotlinx.android.synthetic.main.error_layout.*

class ErrorInfoDialog : BaseActivity() {

    private lateinit var error: String
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.error_layout)
        this.setFinishOnTouchOutside(true)

        // Views
        val errorId = findViewById<TextView>(R.id.errorId)

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            error = extras.getString("error", "Uppss! Ocurrió un error")!!
            title = extras.getString("title", "Lo sentimos, hemos tenido un inconveniente al procesar su solicitud")!!
        }

        errorId.text = error
        errorTitle.text = title
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
package com.syntepro.appbeneficiosbolivia.ui.general

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.R

class InformationDialog : BaseActivity() {

    private lateinit var title:String
    private lateinit var info:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.information_dialog)
        this.setFinishOnTouchOutside(true)

        // Views
        val titleId = findViewById<TextView>(R.id.titleId)
        val infoId = findViewById<TextView>(R.id.infoId)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            title = extras.getString("title","")!!
            info = extras.getString("info", "")!!
        }

        titleId.text = title
        infoId.text = info
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
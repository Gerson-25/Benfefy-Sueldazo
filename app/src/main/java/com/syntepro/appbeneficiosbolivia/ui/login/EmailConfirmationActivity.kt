package com.syntepro.appbeneficiosbolivia.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.entity.app.SocialUser
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_email_confirmation.*

class EmailConfirmationActivity : BaseActivity() {

    private var socialData: SocialUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_email_confirmation)
        this.setFinishOnTouchOutside(false)

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            socialData = extras.getSerializable("model") as SocialUser
        }

        accept.setOnClickListener {
            if (emailId.text.toString().isNotEmpty()) {
                socialData?.email = emailId.text.toString()
                val intent = Intent()
                intent.putExtra("model", socialData)
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            } else Functions.showWarning(this@EmailConfirmationActivity, "Ingrese su correo electrónico para continuar.")
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
}
package com.appbenefy.sueldazo.ui.home.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.entity.service.GetUserRequest
import com.appbenefy.sueldazo.entity.service.UserTokenRequest
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.ui.home.HomeActivity
import com.appbenefy.sueldazo.ui.login.SignUpActivity
import com.appbenefy.sueldazo.ui.login.model.ObtenerTokenRequest
import com.appbenefy.sueldazo.ui.login.model.ValidateTokenRequest
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.Functions.Companion.showError
import com.appbenefy.sueldazo.utils.Underline
import kotlinx.android.synthetic.main.activity_o_t_p_validation.*
import kotlinx.android.synthetic.main.dialog_not_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlinx.android.synthetic.main.activity_o_t_p_validation.loadingScreen as loadingScreen1
import kotlinx.coroutines.launch as launch1

class NotRegisterDialog : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_not_register)

        this.setFinishOnTouchOutside(true)

        close.setOnClickListener {
            finish()
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
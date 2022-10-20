package com.syntepro.appbeneficiosbolivia.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.entity.app.SocialUser
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.login.model.ObtenerTokenRequest
import com.syntepro.appbeneficiosbolivia.ui.login.model.ValidateTokenRequest
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showError
import com.syntepro.appbeneficiosbolivia.utils.Underline
import kotlinx.android.synthetic.gms.activity_credentials.*
import kotlinx.android.synthetic.main.activity_o_t_p_validation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlinx.android.synthetic.main.activity_o_t_p_validation.loadingScreen as loadingScreen1
import kotlinx.coroutines.launch as launch1

class OTPValidationActivity : BaseActivity() {

    private var socialData: SocialUser? = null
    private var sentOptIsActive = true
    var token: String = ""
    var documentId: String = ""
    var cellPhone: String = "-------"
    var alreadyRegister: Boolean = false

    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_o_t_p_validation)

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            documentId = extras.getString("documentId", "")!!
            token = extras.getString("token", "")!!
            cellPhone = extras.getString("cellPhone", "-------")!!
            alreadyRegister = extras.getBoolean("isBenefyClient", false)
        }

        val countDownTimer = object : CountDownTimer(300000, 1000){
            override fun onTick(p0: Long) {
                p0.milliseconds.toComponents { minutes, seconds, nanoseconds ->
                    val minutesFormat = if (minutes<10) "0$minutes" else minutes
                    val secondsFormat = if (seconds<10) "0$seconds" else seconds
                    timerSendOtp.text = "Reenviar token en $minutesFormat:$secondsFormat"
                }
            }

            override fun onFinish() {
                timerSendOtp.Underline(this@OTPValidationActivity, getString(R.string.sent_otp_again))
                sentOptIsActive = true
            }
        }

        timerSendOtp.setOnClickListener {
            if (sentOptIsActive){
                countDownTimer.start()
                sentOptIsActive = false
                obtenerToken()
            }
        }

        accept.setOnClickListener {
            if (otpId.text.toString().isNotEmpty()) {
                showLoading(true)
                validateOtp()
            } else Functions.showError(this@OTPValidationActivity, "Ingrese el código OTP enviado a su número telefónico", "Ingrese OTP")
        }

        sendDisclaimer.text = "Acabamos de enviar un código mediante sms al número $cellPhone, que nos permitirá validar tu cuenta."

        closeOtpValidation.setOnClickListener {
            finish()
        }
    }

    fun timer( time: Long,onComplete: (hours:Int, minutes: Int, seconds: Int) -> Unit){
        for (hours in 0..time){

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

    private fun  validateOtp(){
        val request = ValidateTokenRequest(
            country = "BO",
            language = 1,
            otpCode = otpId.text.toString(),
            token = token
        )
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICROSUELDAZO).create(NetworkService2::class.java)
        scopeMainThread.launch1 {
            try {
                val response = apiService.validateOtp(request)
                when{
                    response.isSuccessful -> {
                        showLoading(false)
                        if (Constants.userProfile == null){
                            val intent = Intent(this@OTPValidationActivity, SignUpActivity::class.java)
                            intent.putExtra("isAnonymousUser", false)
                            intent.putExtra("documentId", documentId)
                            startActivity(intent)
                        } else {
                            if (Constants.userProfile!!.idDocument == documentId){
                                val intent = Intent(this@OTPValidationActivity, HomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this@OTPValidationActivity, SignUpActivity::class.java)
                                intent.putExtra("documentId", documentId)
                                intent.putExtra("isAnonymousUser", false)
                                startActivity(intent)
                            }
                        }

                    }
                    else -> {
                        showError(this@OTPValidationActivity, response.message() ?: getString(R.string.error_inesperado), "Algo salió mal")
                        showLoading(false)
//                        completion(null)
                    }
                }
            } catch (e: Exception) {
                showError(this@OTPValidationActivity,  getString(R.string.error_inesperado), "Algo salió mal")
                showLoading(false)
            }
        }
    }

    private fun obtenerToken(){
        val request = ObtenerTokenRequest (
            country = "BO",
            language = 1,
            documento = documentId
        )
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICROSUELDAZO).create(
            NetworkService2::class.java)
        scopeMainThread.launch1 {
            try {
                val response = apiService.obtenerToken(request)
                when {
                    response.isSuccessful -> {
                        showLoading(false)
                        val ret = response.body()!!
                        if (ret.isSuccess){
                            ret.data?.token?.let {
                                Constants.TOKEN = it
                                token = it
                            }
                        } else {
                            Functions.showError(
                                this@OTPValidationActivity,
                                ret.description ?: getString(R.string.error_inesperado),
                                "Algo salió mal"
                            )
                        }

                    }
                    else -> {
                        Functions.showError(
                            this@OTPValidationActivity,
                            response.message() ?: getString(R.string.error_inesperado),
                            "Algo salió mal"
                        )
                        showLoading(false)
//                        completion(null)
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
//                completion(null)
                Functions.showError(
                    this@OTPValidationActivity,
                    getString(R.string.error_inesperado),
                    "Algo salió mal"
                )
                Log.e("Exception", e.message ?: e.cause?.message ?: e.cause.toString())
            }
        }
    }

    private fun showLoading(show: Boolean){
        loadingScreen1.visibility = if (show) View.VISIBLE else View.GONE
        accept.visibility = if (show) View.GONE else View.VISIBLE
    }

}
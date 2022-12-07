package com.appbenefy.sueldazo.ui.benefy

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.merckers.core.extension.hideKeyboard
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.ui.home.model.RegisterCodeRequest
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.ui.coupon.AgencyActivity
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_promotional_code.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PromotionalCodeActivity : AppCompatActivity() {

    private var commerceId: String = ""
    private var productId: String = ""
    private var allow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_promotional_code)
        this.setFinishOnTouchOutside(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            commerceId = extras.getString("commerceId", "")
            productId = extras.getString("productId", "")
            allow = extras.getBoolean("allowAccess")
            if (!allow) {
                titleId.text = getString(R.string.huawei_promotional)
                timeLimited.visibility = View.VISIBLE
                promotionalLayout.visibility = View.GONE
                agencies.visibility = View.VISIBLE
            }
            val automatic = extras.getBoolean("automaticAssignment", false)
            if (automatic) {
                promotionalField.setText(extras.getString("promotionalCode", ""))
                accept.text = ""
                progress_bar_accept.visibility = View.VISIBLE
                if (validateCode()) {
                    if (promotionalField.text.toString().startsWith("REG")) Log.e("Gift", "Show")
                    getData()
                } else showError()
            }
        }

        accept.setOnClickListener {
            if (allow) {
                hideKeyboard(it)
                accept.text = ""
                progress_bar_accept.visibility = View.VISIBLE
                if (validateCode()) {
                    if (promotionalField.text.toString().startsWith("REG")) Log.e("Gift", "Show")
                    getData()
                } else showError()
            } else finish()
        }

        agencies.setOnClickListener {
            val intent = Intent(this@PromotionalCodeActivity, AgencyActivity::class.java)
            intent.putExtra("commerceId", commerceId)
            intent.putExtra("commerceName", "Huawei")
            intent.putExtra("commerceImage", Constants.HUAWEI_IMAGE)
            intent.putExtra("couponId", productId)
            intent.putExtra("provenance", 0)
            startActivity(intent)
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

    private fun validateCode(): Boolean {
        return if (promotionalField.text.isNullOrEmpty()) {
            promotionalLayout.error = getString(R.string.required_label)
            false
        } else {
            promotionalLayout.error = null
            true
        }
    }

    private fun getData() {
        val request = with(RegisterCodeRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = Functions.getLanguage()
            promCode = promotionalField.text.toString()
            idUser = Constants.userProfile?.idUser ?: ""
            movilBrand = Build.BRAND
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.registerCode(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            val intent = Intent()
                            intent.putExtra("code", true)
                            ret.data?.presentDetail?.let { intent.putExtra("model", it) }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            showError()
                            Toast.makeText(this@PromotionalCodeActivity, ret.description ?: "", Toast.LENGTH_LONG).show()
                        }
                    }
                    else -> {
                        showError()
                        Toast.makeText(this@PromotionalCodeActivity, response.message() ?: "", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                showError()
                Toast.makeText(this@PromotionalCodeActivity, getString(R.string.error_inesperado), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showError() {
        accept.text = getString(R.string.accept)
        progress_bar_accept.visibility = View.GONE
    }

}

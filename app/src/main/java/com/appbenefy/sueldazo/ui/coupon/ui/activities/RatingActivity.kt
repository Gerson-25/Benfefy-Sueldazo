package com.appbenefy.sueldazo.ui.coupon.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.coupon.model.SaveCouponRatingRequest
import com.appbenefy.sueldazo.ui.coupon.viewModel.CouponViewModel
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_rating.*

class RatingActivity : BaseActivity() {

    private lateinit var couponViewModel: CouponViewModel
    private var a = 3
    private var b = 5
    private var couponQRCode: String? = ""
    private var couponID: String? = ""
    private var couponType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_rating)
        this.setFinishOnTouchOutside(true)

        couponViewModel = viewModel(viewModelFactory) {
            observe(saveRating, ::handleCouponRating)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            couponQRCode = extras.getString("qrCode")
            couponID = extras.getString("productId")
            couponType = extras.getInt("productType")
        }

        aceptar_cal.setOnClickListener { v: View ->
            when (v.id) {
                R.id.molesto -> { a = 1 }
                R.id.frustrado -> { a = 2 }
                R.id.indiferente -> { a = 3 }
                R.id.contento -> { a = 4 }
                R.id.muy_contento -> { a = 5 }
            }
            updateRating()
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        omitir_cal.setOnClickListener {
            a = 3
            b = 5
            updateRating()
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.calification_exit))
        builder.setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
            a = 3
            b = 5
            updateRating()
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun onNumberSelected(view: View) {
        when (view.id) {
            R.id.one -> {
                setTextColor()
                one.background = getDrawable(R.drawable.button_shape_selected)
                b = 1
            }
            R.id.two -> {
                setTextColor()
                two.background = getDrawable(R.drawable.button_shape_selected)
                b = 2
            }
            R.id.three -> {
                setTextColor()
                three.background = getDrawable(R.drawable.button_shape_selected)
                b = 3
            }
            R.id.four -> {
                setTextColor()
                four.background = getDrawable(R.drawable.button_shape_selected)
                b = 4
            }
            R.id.five -> {
                setTextColor()
                five.background = getDrawable(R.drawable.button_shape_selected)
                b = 5
            }
            R.id.six -> {
                setTextColor()
                six.background = getDrawable(R.drawable.button_shape_selected)
                b = 6
            }
            R.id.seven -> {
                setTextColor()
                seven.background = getDrawable(R.drawable.button_shape_selected)
                b = 7
            }
            R.id.eight -> {
                setTextColor()
                eight.background = getDrawable(R.drawable.button_shape_selected)
                b = 8
            }
            R.id.nine -> {
                setTextColor()
                nine.background = getDrawable(R.drawable.button_shape_selected)
                b = 9
            }
            R.id.ten -> {
                setTextColor()
                ten.background = getDrawable(R.drawable.button_shape_selected)
                b = 10
            }
        }
    }

    fun onAlignmentSelected(view: View) {
        when (view.id) {
            R.id.molesto -> {
                setImageResource()
                molesto.setImageResource(R.drawable.ic_terrible_selected)
            }
            R.id.frustrado -> {
                setImageResource()
                frustrado.setImageResource(R.drawable.ic_bad_selected)
            }
            R.id.indiferente -> {
                setImageResource()
                indiferente.setImageResource(R.drawable.ic_moreless_selected)
            }
            R.id.contento -> {
                setImageResource()
                contento.setImageResource(R.drawable.ic_good_selected)
            }
            R.id.muy_contento -> {
                setImageResource()
                muy_contento.setImageResource(R.drawable.ic_excelent_selected)
            }
        }
    }

    private fun setImageResource() {
        molesto!!.setImageResource(R.drawable.ic_terrible_unselected)
        frustrado!!.setImageResource(R.drawable.ic_bad_unselected)
        indiferente!!.setImageResource(R.drawable.ic_moreless_unselected)
        contento!!.setImageResource(R.drawable.ic_good_unselected)
        muy_contento!!.setImageResource(R.drawable.ic_excelent_unselected)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTextColor() {
        one!!.background = getDrawable(R.drawable.button_shape)
        two!!.background = getDrawable(R.drawable.button_shape)
        three!!.background = getDrawable(R.drawable.button_shape)
        four!!.background = getDrawable(R.drawable.button_shape)
        five!!.background = getDrawable(R.drawable.button_shape)
        six!!.background = getDrawable(R.drawable.button_shape)
        seven!!.background = getDrawable(R.drawable.button_shape)
        eight!!.background = getDrawable(R.drawable.button_shape)
        nine!!.background = getDrawable(R.drawable.button_shape)
        ten!!.background = getDrawable(R.drawable.button_shape)
    }

    private fun updateRating() {
        val request = SaveCouponRatingRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idProduct = couponID ?: "",
                question1 = a,
                question2 = b,
                idUser = Constants.userProfile?.idUser ?: "",
                qrCode = couponQRCode ?: "",
                idProductType = couponType
        )
        couponViewModel.saveCouponRating(request)
    }

    private fun handleCouponRating(response: BaseResponse<Boolean>?) {
        Log.e("Rating Response", "${response?.data}")
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

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_CANCELED)
    }

}

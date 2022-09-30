package com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.SaveCouponRatingRequest
import com.syntepro.appbeneficiosbolivia.ui.coupon.viewModel.CouponViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
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
        setContentView(R.layout.activity_rating)

        couponViewModel = viewModel(viewModelFactory) {
            observe(saveRating, ::handleCouponRating)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            couponQRCode = extras.getString("qrCode")
            couponID = extras.getString("couponId")
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

    fun onNumberSelected(view: View) {
        when (view.id) {
            R.id.one -> {
                setTextColor()
                one.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 1
            }
            R.id.two -> {
                setTextColor()
                two.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 2
            }
            R.id.three -> {
                setTextColor()
                three.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 3
            }
            R.id.four -> {
                setTextColor()
                four.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 4
            }
            R.id.five -> {
                setTextColor()
                five.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 5
            }
            R.id.six -> {
                setTextColor()
                six.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 6
            }
            R.id.seven -> {
                setTextColor()
                seven.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 7
            }
            R.id.eight -> {
                setTextColor()
                eight.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 8
            }
            R.id.nine -> {
                setTextColor()
                nine.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 9
            }
            R.id.ten -> {
                setTextColor()
                ten.setTextColor(resources.getColor(R.color.colorPrimaryLigth))
                b = 10
            }
        }
    }

    fun onAlignmentSelected(view: View) {
        when (view.id) {
            R.id.molesto -> {
                setImageResource()
                molesto.setImageResource(R.drawable.ic_molesto_select)
            }
            R.id.frustrado -> {
                setImageResource()
                frustrado.setImageResource(R.drawable.ic_frustrado_select)
            }
            R.id.indiferente -> {
                setImageResource()
                indiferente.setImageResource(R.drawable.ic_indiferente_select)
            }
            R.id.contento -> {
                setImageResource()
                contento.setImageResource(R.drawable.ic_contento_select)
            }
            R.id.muy_contento -> {
                setImageResource()
                muy_contento.setImageResource(R.drawable.ic_muy_contento_select)
            }
        }
    }

    private fun setImageResource() {
        molesto!!.setImageResource(R.drawable.ic_molesto)
        frustrado!!.setImageResource(R.drawable.ic_frustrado)
        indiferente!!.setImageResource(R.drawable.ic_indiferente)
        contento!!.setImageResource(R.drawable.ic_contento)
        muy_contento!!.setImageResource(R.drawable.ic_muy_contento)
    }

    private fun setTextColor() {
        one!!.setTextColor(resources.getColor(R.color.black))
        two!!.setTextColor(resources.getColor(R.color.black))
        three!!.setTextColor(resources.getColor(R.color.black))
        four!!.setTextColor(resources.getColor(R.color.black))
        five!!.setTextColor(resources.getColor(R.color.black))
        six!!.setTextColor(resources.getColor(R.color.black))
        seven!!.setTextColor(resources.getColor(R.color.black))
        eight!!.setTextColor(resources.getColor(R.color.black))
        nine!!.setTextColor(resources.getColor(R.color.black))
        ten!!.setTextColor(resources.getColor(R.color.black))
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

}

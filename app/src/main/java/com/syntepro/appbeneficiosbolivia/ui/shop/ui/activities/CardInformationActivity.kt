package com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.merckers.core.extension.hideKeyboard
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ProcessPaymentRequest
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_card_information.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class CardInformationActivity : BaseActivity() {

    private var idOrder: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_information)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            idOrder = extras.getString("orderId", "")
        }

        cardNumberField.addTextChangedListener {
            it?.let { edt ->
                if (edt.isEmpty()) cardNumberId.text = getString(R.string.digits_card)
                else {
                    cardNumberId.text = edt.toString()
                    if (edt.toString().length > 9) cardTypeId.text = Functions.getCreditCardType(edt.toString())
                }
            } ?: run {
                cardNumberId.text = getString(R.string.digits_card)
            }
        }

        expirationDateField.addTextChangedListener {
            it?.let { edt ->
                if (edt.isEmpty()) expirationDateId.text = getString(R.string.expiration_card_date)
                else expirationDateId.text = edt.toString()
            } ?: run {
                expirationDateId.text = getString(R.string.expiration_card_date)
            }
        }

        nameField.addTextChangedListener {
            it?.let { edt ->
                if (edt.isEmpty()) cardNameId.text = getString(R.string.card_holder)
                else cardNameId.text = edt.toString()
            } ?: run {
                cardNameId.text = getString(R.string.card_holder)
            }
        }

        endPurchase.setOnClickListener {
            hideKeyboard(it)
            if (validateData()) {
                endPurchase.text = ""
                progress_bar_accept.visibility = View.VISIBLE
                processPayment()
            } else showError()
        }
    }

    private fun processPayment() {
        val request = ProcessPaymentRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                cardNumber = cardNumberField.text.toString().replace(" ", ""),
                month = expirationDateField.text.toString().substring(0,2).toInt(),
                year = expirationDateField.text.toString().substring(3,7).toInt(),
                nameClient = nameField.text.toString(),
                cvvCard = cvvField.text.toString(),
                idOrderPayment = idOrder,
                idUser = Constants.userProfile?.idUser ?: ""
        )
        val apiServe = RetrofitClientInstance.getClient(Constants.BASE_URL_PAYMENT_GATEWAY)
        val inst = apiServe.create(NetworkService2::class.java)
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        scopeMainThread.launch {
            try {
                val ret = inst.processPayment(request)
                if (ret.isSuccessful) {
                    val response = ret.body()!!
                    if (response.data) {
//                        val intent = Intent(this@CardInformationActivity, SuccessPaymentActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
                        val intent = Intent(this@CardInformationActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("paid", true)
                        startActivity(intent)
                    } else {
                        Functions.showWarning(this@CardInformationActivity, response.description
                                ?: "Ocurrió un error")
                        showError()
                    }
                } else {
                    Functions.showWarning(this@CardInformationActivity, ret.message()
                            ?: "Ocurrió un error")
                    Log.e("Error", ret.message())
                    showError()
                }
            } catch (e: Exception) {
                showError()
                Functions.showWarning(this@CardInformationActivity, "Ocurrió un error, intenta de nuevo.")
                Log.e("Error", "Ocurrió un error : ${e.printStackTrace()}")
            }
        }
    }

    private fun validateData(): Boolean {
        return when {
            cardNumberField.text.isNullOrEmpty() -> {
                cardNumberLayout.error = getString(R.string.c_required)
                expirationDateLayout.error = null
                nameLayout.error = null
                cvvLayout.error = null
                false
            }
            cardNumberField.text!!.length < 19 -> {
                cardNumberLayout.error = "Incompleto"
                expirationDateLayout.error = null
                nameLayout.error = null
                cvvLayout.error = null
                false
            }
            expirationDateField.text.isNullOrEmpty() -> {
                cardNumberLayout.error = null
                expirationDateLayout.error = getString(R.string.c_required)
                nameLayout.error = null
                cvvLayout.error = null
                false
            }
            expirationDateField.text!!.length < 7 -> {
                cardNumberLayout.error = null
                expirationDateLayout.error = "Incompleto"
                nameLayout.error = null
                cvvLayout.error = null
                false
            }
            nameField.text.isNullOrEmpty() -> {
                cardNumberLayout.error = null
                expirationDateLayout.error = null
                nameLayout.error = getString(R.string.c_required)
                cvvLayout.error = null
                false
            }
            cvvField.text.isNullOrEmpty() -> {
                cardNumberLayout.error = null
                expirationDateLayout.error = null
                nameLayout.error = null
                cvvLayout.error = getString(R.string.c_required)
                false
            }
            cvvField.text!!.length < 3 -> {
                cardNumberLayout.error = null
                expirationDateLayout.error = null
                nameLayout.error = null
                cvvLayout.error = "Incompleto"
                false
            }
            else -> {
                cardNumberLayout.error = null
                expirationDateLayout.error = null
                nameLayout.error = null
                cvvLayout.error = null
                true
            }
        }
    }

    private fun showError() {
        endPurchase.text = getString(R.string.end_purchase)
        progress_bar_accept.visibility = View.GONE
    }

}
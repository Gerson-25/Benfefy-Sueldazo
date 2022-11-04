package com.syntepro.sueldazo.ui.shop.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.service.NetworkService2
import com.syntepro.sueldazo.service.RetrofitClientInstance
import com.syntepro.sueldazo.ui.shop.model.DistanceAmountDeliveryRequest
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_invoice.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class InvoiceActivity : BaseActivity() {

    private val format = DecimalFormat("###,##0.0#")
    private var articleTotal: Double = 0.0
    private var deliveryTotal: Double = 0.0
    private var deliveryDistance: Double = 0.0
    private var idDispatchPoint: String = ""
    private var userDeliveryLatitude: Double = 0.0
    private var userDeliveryLongitude: Double = 0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.payment_tb)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            articleTotal = extras.getDouble("articleTotal", 0.0)
            deliveryTotal = extras.getDouble("deliveryTotal", 0.0)
            val floatDistance = extras.getFloat("deliveryDistance", 0.0f)
            deliveryDistance = floatDistance.toDouble()
            idDispatchPoint = extras.getString("idDispatchPoint", "")
            userDeliveryLatitude = extras.getDouble("userDeliveryLatitude")
            userDeliveryLongitude = extras.getDouble("userDeliveryLongitude")

            fetchDistanceAmountDelivery {
                if (!it) manualCalculateDelivery()
                else manualCalculateDelivery()
            }
        }

        articleOnly.setOnCheckedChangeListener { _, b ->
            if (b) {
                deliveryId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(0.0)}"
                totalId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(articleTotal)}"
            }
        }

        includeDelivery.setOnCheckedChangeListener { _, b ->
            if (b) {
                deliveryId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(deliveryTotal)}"
                val total = articleTotal + deliveryTotal
                totalId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(total)}"
            }
        }

        next.setOnClickListener {
            if (articleOnly.isChecked) {
                if (deliveryConditions.isChecked) {
                    val intent = Intent()
                    intent.putExtra("includeDelivery", false)
                    setResult(Activity.RESULT_OK, intent)
                    this.finish()
                } else Functions.showWarning(this@InvoiceActivity, getString(R.string.accept_delivery_payment))
            } else {
                val intent = Intent()
                intent.putExtra("includeDelivery", true)
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchDistanceAmountDelivery(completion: (Boolean) -> Unit) {
        val request = DistanceAmountDeliveryRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idDispatchPoint = idDispatchPoint,
                productPrice = articleTotal,
                userLat = userDeliveryLatitude,
                userLon = userDeliveryLongitude
        )
        val apiServe = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2)
        val inst = apiServe.create(NetworkService2::class.java)
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        scopeMainThread.launch {
            try {
                val ret = inst.getDistanceAmountDelivery(request)
                if (ret.isSuccessful) {
                    val response = ret.body()!!
                    response.data?.let {
                        deliveryDistanceId.text = "${format.format(it.distance)} Km"
                        completion(true)
                    } ?: run {
                        completion(false)
                    }
                } else {
                    completion(false)
                    Log.e("Error", ret.message())
                }
            } catch (e: Exception) {
                completion(false)
                Log.e("Error", "Ocurrió un error : ${e.printStackTrace()}")
            }
        }
    }

    private fun manualCalculateDelivery() {
        articlePriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(articleTotal)}"
        val total = articleTotal + deliveryTotal
        articleDeliveryId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(total)}"
        subtotalId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(articleTotal)}"
        deliveryId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(0.0)}"
        totalId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(articleTotal)}"

        deliveryDistanceId.text = "${format.format(deliveryDistance)} Km"
    }

}
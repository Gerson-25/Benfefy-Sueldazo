package com.syntepro.appbeneficiosbolivia.ui.lealtad

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.merckers.core.exception.Failure
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.AffiliateToPlanRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.AffiliateToPlanResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel.LoyaltyPlanViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_terms.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TermsActivity : BaseActivity() {

    private lateinit var loyaltyViewModel: LoyaltyPlanViewModel
    private lateinit var roomDataBase: RoomDataBase
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
    private var mPlanId: String? = null
    private var mCode: String? = null
    private var mAccepted = false
    private var timeZoneUser: String? = null
    private var affiliate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_terms)

        loyaltyViewModel = viewModel(viewModelFactory) {
            observe(planDetail, ::planResp)
            observe(affiliate, ::affiliateResponse)
            failure(failure, ::handleError)
        }

        // ROOM
        roomDataBase = RoomDataBase.getRoomDatabase(this@TermsActivity)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            mPlanId = extras.getString("planId")
            mCode = extras.getString("mCode", "")
            affiliate = extras.getBoolean("affiliate")
            val planName = extras.getString("planName")
            supportActionBar!!.title = planName
            planNameId.text = planName
            descId.text =  extras.getString("planDesc")?:""
            commerceNameId.text = extras.getString("mCommerceName")?:""
            Picasso.get().load(extras.getString("mCommerceImage")).into(commerceId)
            commerceCategoryId.text = extras.getString("mRubroName")
        }

        joinId.setOnClickListener {
            if (checkbox_id.isChecked) addLoyaltyUserPlan()
            else {
                val dialogError = AlertDialog.Builder(this)
                dialogError.setTitle(R.string.warning_message)
                        .setMessage(resources.getString(R.string.must_accept_terms))
                        .setCancelable(false)
                        .setPositiveButton(R.string.close_label) { _, _ -> }.show()
            }
        }

        // Get User Time Zone
        val cu = roomDataBase.accessDao().country
        timeZoneUser = cu.timeZone

        // Show Data
        getData()
    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }

    private fun returnResult(error: String?) {
        val data = Intent()
        error.let { data.putExtra("error", error) }
        if (mAccepted)
            setResult(Activity.RESULT_OK, data)
        else
            setResult(Activity.RESULT_CANCELED, data)

        finish()
    }

    private fun getData() {
        val request = PlanDetailRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idPlan = mPlanId!!
        )
        loyaltyViewModel.getPlanDetail(request)
    }

    private fun planResp(detail: BaseResponse<PlanDetailResponse>?) {
        detail?.let {
            if (it.isSuccess) {
                termId.text = it.data?.termsAndConditions
                membersId.text = it.data?.activeUsers.toString()
                formatter.timeZone = TimeZone.getTimeZone(timeZoneUser)
                val dateNow = formatter.format(it.data?.endDate!!)
                countDown(dateNow)
                if (!affiliate) {
                    checkbox_id.visibility = View.GONE
                    joinId.visibility = View.GONE
                }
            }
        }
    }

    private fun affiliateResponse(response: BaseResponse<AffiliateToPlanResponse>?) {
        response?.let{
                returnResult(if(response.isSuccess) null else response.description)
            loyaltyViewModel.affiliate.value = null
        }
    }

    override fun handleError(failure: Failure?) {
        super.handleError(failure)
        progress_bar.visibility = View.GONE
    }

    private fun countDown(fin: String) {
        val t: Thread = object : Thread() {
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            try {
                                formatter.timeZone = TimeZone.getTimeZone(timeZoneUser)
                                val currentTime = Calendar.getInstance()
                                val dateNow = formatter.format(currentTime.time)
                                @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
                                val dateI: Date
                                val dateF: Date
                                val now = simpleDateFormat.parse(dateNow)
                                dateF = simpleDateFormat.parse(fin)!!
                                dateI = now!!
                                val dif: String = getFechas(dateI, dateF)!!
                                counterId.text = dif
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (ie: InterruptedException) {
                    ie.printStackTrace()
                }
            }
        }
        t.start()
    }

    private fun getFechas(fechaInicial: Date, fechaFinal: Date): String {
        var diferencia = fechaFinal.time - fechaInicial.time
        val segsMilli: Long = 1000
        val minsMilli = segsMilli * 60
        val horasMilli = minsMilli * 60
        val diasMilli = horasMilli * 24
        val diasTranscurridos = diferencia / diasMilli
        diferencia %= diasMilli
        val horasTranscurridos = diferencia / horasMilli
        diferencia %= horasMilli
        val minutosTranscurridos = diferencia / minsMilli
        diferencia %= minsMilli
        val segsTranscurridos = diferencia / segsMilli
        return String.format(getString(R.string.dias_transcurridos_label), diasTranscurridos.toString(), horasTranscurridos.toString()
                + ": " + minutosTranscurridos.toString() + ": " + segsTranscurridos.toString() + "s")
    }

    private fun addLoyaltyUserPlan() {
        progress_bar.visibility = View.VISIBLE
        val request = AffiliateToPlanRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idPlan = mPlanId!!,
                idUser =  Constants.userProfile?.idUser ?: "",
                nameUser = Constants.userProfile?.names  + " " +  Constants.userProfile?.lastNames,
                planCode = mCode
        )
        loyaltyViewModel.affiliateToPlan(request)
        mAccepted = true
    }
}

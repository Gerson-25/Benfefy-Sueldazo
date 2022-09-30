package com.syntepro.appbeneficiosbolivia.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.entity.firebase.TarjetaSellos
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.CardDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.CardDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.LoyaltyInfoFragment
import com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel.LoyaltyPlanViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.main.stamp_card_info_layout.*
import java.text.DateFormat
import java.util.*

class StampCardInfoDialog: BaseActivity() {

    private lateinit var loyaltyViewModel: LoyaltyPlanViewModel
    private var canceled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        appComponent.inject(this)
        setContentView(R.layout.stamp_card_info_layout)
        this.setFinishOnTouchOutside(true)

        loyaltyViewModel = viewModel(viewModelFactory) {
            observe(cardDetail, ::cardResponse)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val mPlan = extras.getString("planId")
            val mStampId = extras.getString("stampId")

            // Show Data
            getData(mStampId ?: "")
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

    override fun onBackPressed() {
        val data = Intent()
        if(!canceled)
            data.putExtra("refresh", true)
        setResult(RESULT_OK, data)
        super.onBackPressed()
        finish()
    }

    private fun getData(id: String) {
        val request = CardDetailRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idCard = id
        )
        loyaltyViewModel.getCardDetail(request)
    }

    private fun cardResponse(response: BaseResponse<CardDetailResponse>?) {
        statusId.text = Functions.fromHtml(String.format(getString(R.string.status_label), response?.data?.state))
        actualStampsId.text = "${response?.data?.obtainedStamp}/${response?.data?.requiredStamp}"
        startDateId.text = Functions.fromHtml(String.format(getString(R.string.start_date_label), Helpers.dateToStr(response?.data?.stardDate ?: Date(), DateFormat.MEDIUM)))
        endDateId.text = Functions.fromHtml(String.format(getString(R.string.end_date_label),
                if(response?.data?.stardDate != null) Helpers.dateToStr(response.data.endDate, DateFormat.MEDIUM) else "-"))
        dateCompletedId.text = Functions.fromHtml(String.format(getString(R.string.completed_date_label),
                if(response?.data?.dateCompleted != null) Helpers.dateToStr(response.data.dateCompleted, DateFormat.MEDIUM) else "-"))
        exchangeDateId.text = Functions.fromHtml(String.format(getString(R.string.exchange_date_label),
                if(response?.data?.exchangeDate != null) Helpers.dateToStr(response.data.exchangeDate, DateFormat.MEDIUM) else "-"))
        dateLastStampId.text = Functions.fromHtml(String.format(getString(R.string.last_stamp_date_label),
                if(response?.data?.dateLastStamp != null) Helpers.dateToStr(response.data.dateLastStamp, DateFormat.MEDIUM) else "-"))
    }

    private fun getStatus(status: Int): String {
        var st = ""
        var color = "black"
        when(status) {
            TarjetaSellos.STATUS_INPROGRESS -> {
                st =  getString(R.string.stamp_status_inprogress)
            }
            TarjetaSellos.STATUS_COMPLETED -> {
                st =  getString(R.string.stamp_status_completed)
                color = "#02B256"
            }
            TarjetaSellos.STATUS_EXPIRED -> {
                st =  getString(R.string.stamp_status_expired)
                color = "#f76B1C"
            }
            TarjetaSellos.STATUS_REDEEMED -> {
                st =  getString(R.string.stamp_status_redimeed)
                color = "#4C84FF"
            }
        }
        return String.format(getString(R.string.stamp_status, color, st))
    }

}
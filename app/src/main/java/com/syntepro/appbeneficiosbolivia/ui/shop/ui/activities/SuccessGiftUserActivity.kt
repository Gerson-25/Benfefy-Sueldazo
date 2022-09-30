package com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.entity.service.PresentDetail
import com.syntepro.appbeneficiosbolivia.ui.home.model.PurchasedProductsResponse
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.main.activity_success_gift_user.*
import java.text.DateFormat
import java.text.DecimalFormat

class SuccessGiftUserActivity : BaseActivity() {

    private val format = DecimalFormat("###,##0.0#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_success_gift_user)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val model = extras.getSerializable("model") as? PresentDetail
            showData(model)
        }

        accept.setOnClickListener { this.finish() }
    }

    override fun onBackPressed() { this.finish() }

    private fun showData(model: PresentDetail?) {
        model?.let {
            val header = "${getString(R.string.first_gift_user)} ${it.commerceName}${getString(R.string.second_gift_user)}"
            giftHeaderId.text = header
            Functions.showImage(it.urlImage, imageId)
            titleId.text = it.title
            subtitleId.text = it.subtitle
            if (it.idProductType == PurchasedProductsResponse.GIFT_CARD_GIFT) couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(it.amount)}" else couponPriceId.visibility = View.GONE
            fromId.text = Functions.fromHtml(String.format(getString(R.string.from_label), it.userGivesName))
            toId.text = Functions.fromHtml(String.format(getString(R.string.to_label), it.userReceivesName))
            dedicationId.text = "\"${it.dedicatory}\""
            availableId.text = Functions.fromHtml(String.format(getString(R.string.available_label), Helpers.dateToStr(it.presentEndDate, DateFormat.DATE_FIELD)))
        }
    }

}
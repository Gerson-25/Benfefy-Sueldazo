package com.syntepro.sueldazo.ui.shop.ui.activities

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.entity.service.PresentDetail
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import com.syntepro.sueldazo.utils.Helpers
import kotlinx.android.synthetic.main.activity_coupon_gift_detail.*
import java.text.DateFormat
import java.text.DecimalFormat

class CouponGiftDetailActivity : BaseActivity() {

    private val format = DecimalFormat("###,##0.0#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_gift_detail)

        val extras = intent.extras
        if (extras != null) {
            val model = extras.getSerializable("model") as? PresentDetail
            showGiftDetail(model)
        }

        accept.setOnClickListener { this.finish() }
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

    private fun showGiftDetail(model: PresentDetail?) {
        model?.let {
            if (it.amount != 0.0) {
                couponPriceId.visibility = View.VISIBLE
                couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(it.amount)}"
                amountId.visibility = View.VISIBLE
            }
            dateId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_date_label), Helpers.dateToStr(it.presentEndDate, DateFormat.LONG)))
            articleId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_article_label), it.title))
            commerceId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_commerce_label), it.commerceName))
            fromId.text = Functions.fromHtml(String.format(getString(R.string.from_label), it.userGivesName))
            toId.text = Functions.fromHtml(String.format(getString(R.string.to_label), it.userReceivesName))
            dedicationId.text = Functions.fromHtml(String.format(getString(R.string.gift_detail_dedication_label), it.dedicatory))
        }
    }

}
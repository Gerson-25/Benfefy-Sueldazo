package com.syntepro.sueldazo.ui.coupon.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.coupon.model.CouponListResponse
import com.syntepro.sueldazo.ui.coupon.ui.CouponListDiff
import com.syntepro.sueldazo.ui.coupon.ui.activities.CouponListActivity
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.coupon_mosaic_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class CouponMosaicAdapter @Inject constructor() :
        PagedListAdapter<CouponListResponse, CouponMosaicAdapter.ViewHolder>(
                CouponListDiff()
        ) {

    private var activity: CouponListActivity? = null
    private val format = DecimalFormat("###,##0.0#")

    internal var collection: List<CouponListResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size
    fun setActivity(activity: CouponListActivity) { this.activity = activity }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coupon_mosaic_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it, position) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: CouponListResponse, position: Int) {

            if (model.available) {
                view.blockedView.visibility = View.GONE
                view.blockedImage.visibility = View.GONE
            } else {
                view.blockedView.visibility = View.VISIBLE
                view.blockedImage.visibility = View.VISIBLE
            }

            Functions.showImage(model.urlImageCoupon, itemView.couponImageId)
            Functions.showRoundedImage(model.urlImageCommerce, view.commerceImageId)
            view.titleCouponId.text = model.title
            view.subtitleCouponId.text = model.subtitle
            when(model.firebaseCodeType) {
                1 -> {
                    val discount = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.discountPrice)}"
                    view.couponOriginalPriceId.text = Html.fromHtml("<del>${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.realPrice)}</del>")
                    view.couponPriceId.text = discount
                }
                2 -> {
                    val discount = "${format.format(model.discountPrice)} %"
                    view.couponOriginalPriceId.text = ""
                    view.couponPriceId.text = discount
                }
                3 -> {
                    val discount = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.discountPrice)}"
                    view.couponOriginalPriceId.text = ""
                    view.couponPriceId.text = discount
                }
                5 -> {
                    val discount = "${view.context.getString(R.string.seal_label)}: ${model.realPrice.toInt()}"
                    view.couponOriginalPriceId.text = ""
                    view.couponPriceId.text = discount
                }
                6 -> {
                    val discount = "${view.context.getString(R.string.miles_label)}: ${model.realPrice.toInt()}"
                    view.couponOriginalPriceId.text = ""
                    view.couponPriceId.text = discount
                }
            }

            if (model.favorite) {
                val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
                view.favorite.startAnimation(animScale)
                view.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
            } else
                view.favorite.setBackgroundResource(R.drawable.ic_mark_fav)

            if (model.vip) view.vip.visibility = View.VISIBLE else view.vip.visibility = View.GONE

            view.favorite.setOnClickListener { activity?.favorite(model, position) }
            view.setOnClickListener {
                if (model.available) activity?.openDetail(model.idCoupon, model.firebaseCodeType)
                else Functions.showWarning(view.context, view.context.getString(R.string.blocked_coupon))
            }
        }
    }

}
package com.appbenefy.sueldazo.ui.home.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponResponse
import com.appbenefy.sueldazo.ui.coupon.ui.FeaturedCouponDiff
import com.appbenefy.sueldazo.ui.home.ui.fragments.FavoriteFragment
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.UserType
import kotlinx.android.synthetic.main.best_discounts_item.view.*
import kotlinx.android.synthetic.main.featured_item.view.*
import kotlinx.android.synthetic.main.featured_item.view.blockedCoupon
import kotlinx.android.synthetic.main.featured_item.view.commerceImageId
import kotlinx.android.synthetic.main.featured_item.view.itemId
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class FeaturedAdapter@Inject constructor() :
        PagedListAdapter<FeaturedCouponResponse, FeaturedAdapter.ViewHolder>(
                FeaturedCouponDiff()
        ) {

    private var fragment: FavoriteFragment? = null
    private val format = DecimalFormat("###,##0.0#")

    fun parentFragment(fragment: FavoriteFragment) { this.fragment = fragment }

    internal var collection: List<FeaturedCouponResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.featured_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it, position) }
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: FeaturedCouponResponse, position: Int) {
            Functions.showImage(model.couponImage, view.couponImageId)
            Functions.showRoundedImage(model.commerceImage, view.commerceImageId)
            view.titleCouponId.text = model.title
            view.subtitleCouponId.text = model.commerceName

            when(Constants.TYPE_OF_USER){
                UserType.VERIFIED_USER -> view.blockedCoupon.visibility = View.GONE
                else -> view.blockedCoupon.visibility = View.VISIBLE
            }

            when(model.couponType) {
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
                else -> {
                    val discount = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.discountPrice)}"
                    view.couponOriginalPriceId.text = Html.fromHtml("<del>${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.realPrice)}</del>")
                    view.couponPriceId.text = discount
                }
            }

            view.itemId.setOnClickListener {
                if (fragment?.showBlocked() == true){
                    fragment?.notRegisterUser()
                } else {
                    fragment?.openBestDiscountDetail(model.idCoupon)
                }
            }
            view.favorite.setOnClickListener { fragment?.favorite(model, position) }
        }
    }

}
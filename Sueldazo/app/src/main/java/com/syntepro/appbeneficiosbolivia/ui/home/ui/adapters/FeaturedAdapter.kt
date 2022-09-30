package com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.FeaturedCouponResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.FeaturedCouponDiff
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.FavoriteFragment
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.featured_item.view.*
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
            view.subtitleCouponId.text = model.subtitle
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

            if (model.favorite) {
                val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
                view.favorite.startAnimation(animScale)
                view.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
            } else
                view.favorite.setBackgroundResource(R.drawable.ic_mark_fav)

            view.itemId.setOnClickListener { fragment?.openBestDiscountDetail(model.idCommerce) }
            view.favorite.setOnClickListener { fragment?.favorite(model, position) }
        }
    }

}
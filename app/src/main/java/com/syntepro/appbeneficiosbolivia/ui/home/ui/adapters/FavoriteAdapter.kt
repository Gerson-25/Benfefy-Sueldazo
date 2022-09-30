package com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.FavoriteFragment
import com.syntepro.appbeneficiosbolivia.ui.home.model.FavoriteResponse
import com.syntepro.appbeneficiosbolivia.ui.home.ui.FavoriteDiff
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.coupon_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class FavoriteAdapter @Inject constructor() :
        PagedListAdapter<FavoriteResponse, FavoriteAdapter.ViewHolder>(
                FavoriteDiff()
        ) {

    private var fragment: FavoriteFragment? = null
    private val format = DecimalFormat("###,##0.0#")

    fun parentFragment(fragment: FavoriteFragment) { this.fragment = fragment }

    internal var collection: List<FavoriteResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coupon_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: FavoriteResponse) {
            Functions.showImage(model.urlCoupon, view.couponImageId)
            view.titleCouponId.text = model.title
            view.subtitleCouponId.text = model.subTitle
            when (model.idCouponType) {
                1 -> {
                    when(model.fbCodeType) {
                        1 -> {
                            val discount = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                            view.couponOriginalPriceId.text = Html.fromHtml("<del>${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.regularPrice)}</del>")
                            view.couponPriceId.text = discount
                        }
                        2 -> {
                            val discount = "${format.format(model.percentPrice)} %"
                            view.couponOriginalPriceId.text = ""
                            view.couponPriceId.text = discount
                        }
                        3 -> {
                            val discount = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                            view.couponOriginalPriceId.text = ""
                            view.couponPriceId.text = discount
                        }
                        5 -> {
                            val discount = "${view.context.getString(R.string.seal_label)}: ${model.regularPrice.toInt()}"
                            view.couponOriginalPriceId.text = ""
                            view.couponPriceId.text = discount
                        }
                        6 -> {
                            val discount = "${view.context.getString(R.string.miles_label)}: ${model.regularPrice.toInt()}"
                            view.couponOriginalPriceId.text = ""
                            view.couponPriceId.text = discount
                        }
                    }
                }
                2 -> {
                    val discount = (model.regularPrice) * (model.percentPrice/100.0f)
                    val newPrice = model.regularPrice - discount
                    view.couponOriginalPriceId.text = Html.fromHtml("<del>${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.regularPrice)}</del>")
                    view.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(newPrice)}"
                }
                3 -> {
                    view.couponOriginalPriceId.text = ""
                    view.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.regularPrice)}"
                }
            }

            if (model.available) {
                view.blockedView.visibility = View.GONE
                view.blockedImage.visibility = View.GONE
            } else {
                view.blockedView.visibility = View.VISIBLE
                view.blockedImage.visibility = View.VISIBLE
            }

            val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
            view.favorite.startAnimation(animScale)
            view.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)

            view.setOnClickListener {
                val loyaltyType = if (model.fbCodeType == 5) 2 else if (model.fbCodeType == 6) 1 else 0
                if (model.available) fragment?.openDetail(model.idCouponType, model.idCoupon, loyaltyType)
                else Functions.showWarning(view.context, view.context.getString(R.string.blocked_coupon))
            }
        }
    }

}
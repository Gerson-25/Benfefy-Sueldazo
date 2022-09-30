package com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.BestDiscountResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.BestDiscountDiff
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.FavoriteFragment
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.best_discounts_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class BestDiscountAdapter@Inject constructor() :
        PagedListAdapter<BestDiscountResponse, BestDiscountAdapter.ViewHolder>(
                BestDiscountDiff()
        ) {

    private var fragment: FavoriteFragment? = null
    private val format = DecimalFormat("###,##0.0#")

    fun parentFragment(fragment: FavoriteFragment) { this.fragment = fragment }

    internal var collection: List<BestDiscountResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.best_discounts_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: BestDiscountResponse) {
            Functions.showImage(model.banner, view.bannerImageId)
            Functions.showRoundedImage(model.commerceImage, view.commerceImageId)
            when (model.couponType) {
                1 -> {
                    val discount = (model.discountPrice / model.realPrice) * 100
                    val percent = 100 - discount
                    view.discountPercentageId.text = "${percent.toInt()}%"
                }
                2 -> view.discountPercentageId.text = "${model.discountPrice.toInt()}%"
                3 -> view.discountPercentageId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.discountPrice.toInt())}"
                else -> view.discountPercentageId.text = "0%"
            }

            view.itemId.setOnClickListener { fragment?.openBestDiscountDetail(model.idCommerce) }
        }
    }

}
package com.syntepro.sueldazo.ui.home.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.home.ui.fragments.BenefyFragment
import com.syntepro.sueldazo.ui.home.model.PurchasedProductsResponse
import com.syntepro.sueldazo.ui.home.ui.PurchaseProductDiff
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.purchased_product_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class ProductsAdapter @Inject constructor() :
        PagedListAdapter<PurchasedProductsResponse, ProductsAdapter.ViewHolder>(
                PurchaseProductDiff()
        ) {

    private var fragment: BenefyFragment? = null
    private val format = DecimalFormat("###,##0.0#")

    fun parentFragment(fragment: BenefyFragment) { this.fragment = fragment }

    internal var collection: List<PurchasedProductsResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.purchased_product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: PurchasedProductsResponse) {
            Functions.showImage(model.urlArt, itemView.couponImageId)
            itemView.titleCouponId.text = model.title
            itemView.subtitleCouponId.text = model.subtitle
            itemView.couponOriginalPriceId.text = Html.fromHtml("${Constants.userProfile?.currency ?: "Bs"} <del>${format.format(model.regularPrice)}</del>")
            itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
            if (model.percentPrice.equals(model.regularPrice)) itemView.couponOriginalPriceId.text = ""

            when (model.idProductType) {
                PurchasedProductsResponse.PURCHASED_COUPON -> itemView.productTypeId.text = itemView.context.getString(R.string.purchased_coupon)
                PurchasedProductsResponse.PURCHASED_GIFT_CARD -> {
                    itemView.productTypeId.text = itemView.context.getString(R.string.purchased_giftcard)
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                }
                PurchasedProductsResponse.WELCOME_COUPON -> {
                    itemView.productTypeId.text = itemView.context.getString(R.string.welcome)
                    val discount = "${format.format(model.percentPrice)} %"
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = discount
                }
                PurchasedProductsResponse.PROMOTIONAL_COUPON -> itemView.productTypeId.text = itemView.context.getString(R.string.promotional)
                PurchasedProductsResponse.CORPORATE_VOUCHER -> {
                    itemView.productTypeId.text = itemView.context.getString(R.string.corporate_voucher)
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.regularPrice)}"
                    itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                }
                PurchasedProductsResponse.ARTICLE_GIFT -> {
                    itemView.productTypeId.text = itemView.context.getString(R.string.given_item)
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = ""
                }
                PurchasedProductsResponse.GIFT_CARD_GIFT -> {
                    itemView.productTypeId.text = itemView.context.getString(R.string.given_item)
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.percentPrice)}"
                }
                PurchasedProductsResponse.INSURANCE_GIFT -> {
                    itemView.productTypeId.text = itemView.context.getString(R.string.given_item)
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = ""
                }
                else -> itemView.productTypeId.text = itemView.context.getString(R.string.promotional)
            }

            if (model.blocked) {
                itemView.blockedView.visibility = View.VISIBLE
                itemView.blockedImage.visibility = View.VISIBLE
                itemView.couponOriginalPriceId.visibility = View.INVISIBLE
                itemView.couponPriceId.visibility = View.INVISIBLE
            } else {
                itemView.blockedView.visibility = View.GONE
                itemView.blockedImage.visibility = View.GONE
                itemView.couponOriginalPriceId.visibility = View.VISIBLE
                itemView.couponPriceId.visibility = View.VISIBLE
            }

            if (model.favorite) {
                val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
                itemView.favorite.startAnimation(animScale)
                itemView.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
            } else
                itemView.favorite.setBackgroundResource(R.drawable.ic_mark_fav)

            itemView.itemId.setOnClickListener { fragment?.openProduct(model) }
        }
    }

}
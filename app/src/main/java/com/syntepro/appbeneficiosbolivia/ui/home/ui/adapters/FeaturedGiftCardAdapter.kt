package com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.HomeFragment
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.GiftCardDiff
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.gift_cards_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class FeaturedGiftCardAdapter@Inject constructor() :
        PagedListAdapter<GiftCard, FeaturedGiftCardAdapter.ViewHolder>(
                GiftCardDiff()
        ) {

    private var fragment: HomeFragment? = null

    fun parentFragment(fragment: HomeFragment) { this.fragment = fragment }

    internal var collection: List<GiftCard> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.featured_gift_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(model: GiftCard) {
            model.color?.let {
                if (it.isNotEmpty() && it.startsWith("#"))
                    itemView.backgroundGiftCardColorId.setBackgroundColor(Color.parseColor(model.color))
            }
            Functions.showRoundedImage(model.urlCommerce, itemView.commerceImageId)
            itemView.amountId.text = "${Constants.userProfile?.currency ?: "Bs"} ${model.minimumAmount.toInt()}"

            itemView.itemId.setOnClickListener { fragment?.openGiftCardDetail(model.idGiftcard ?: "") }
        }
    }

}
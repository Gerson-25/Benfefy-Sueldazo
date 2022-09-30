package com.syntepro.appbeneficiosbolivia.ui.shop.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.GiftCardDiff
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.gift_card_item.view.*

class GiftCardAdapter(val adapterOnClick : (Any) -> Unit):
        PagedListAdapter<GiftCard, GiftCardAdapter.ViewHolder>(GiftCardDiff()) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gift_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
    ) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(model: GiftCard) {
            Functions.showImage(model.urlCommerce, itemView.commerceImageId)
            itemView.amountId.text = "${Constants.userProfile?.currency ?: "Bs"} ${model.minimumAmount}"
            if (model.bonus != 0.0) itemView.bonusId.text = "${model.bonus}%" else itemView.bonusId.visibility = View.GONE

            itemView.itemId.setOnClickListener { adapterOnClick(model) }
        }
    }
}
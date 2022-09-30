package com.syntepro.appbeneficiosbolivia.ui.shop.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard

class GiftCardDiff: DiffUtil.ItemCallback<GiftCard>() {
    override fun areItemsTheSame(oldItem: GiftCard, newItem: GiftCard): Boolean {
        return oldItem.idGiftcard == newItem.idGiftcard
    }

    override fun areContentsTheSame(oldItem: GiftCard, newItem: GiftCard): Boolean {
        return oldItem.idGiftcard == newItem.idGiftcard
                && oldItem.idGiftcard == newItem.idGiftcard
    }
}
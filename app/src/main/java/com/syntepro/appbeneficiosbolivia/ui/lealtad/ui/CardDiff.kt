package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Card

class CardDiff : DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.idCard == newItem.idCard
    }

    override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem == newItem
    }

}
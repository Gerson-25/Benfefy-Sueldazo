package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Commerce


class CommerceDiff : DiffUtil.ItemCallback<Commerce>() {
    override fun areItemsTheSame(oldItem: Commerce, newItem: Commerce): Boolean {
        return oldItem.idComercio == newItem.idComercio
    }

    override fun areContentsTheSame(oldItem: Commerce, newItem: Commerce): Boolean {
        return oldItem.idComercio == newItem.idComercio
                && oldItem.categoryName == newItem.categoryName
    }

}
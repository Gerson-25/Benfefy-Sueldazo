package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.MilesCoupon

class MilesDiff: DiffUtil.ItemCallback<MilesCoupon>() {
    override fun areItemsTheSame(oldItem: MilesCoupon, newItem: MilesCoupon): Boolean {
        return oldItem.idCampana == newItem.idCampana
    }

    override fun areContentsTheSame(oldItem: MilesCoupon, newItem: MilesCoupon): Boolean {
        return oldItem == newItem
    }

}
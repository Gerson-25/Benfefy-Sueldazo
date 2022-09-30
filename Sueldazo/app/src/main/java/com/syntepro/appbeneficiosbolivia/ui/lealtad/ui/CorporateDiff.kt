package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.CorporateCoupon

class CorporateDiff : DiffUtil.ItemCallback<CorporateCoupon>() {
    override fun areItemsTheSame(oldItem: CorporateCoupon, newItem: CorporateCoupon): Boolean {
        return oldItem.idCampana == newItem.idCampana
    }

    override fun areContentsTheSame(oldItem: CorporateCoupon, newItem: CorporateCoupon): Boolean {
        return oldItem == newItem
    }

}
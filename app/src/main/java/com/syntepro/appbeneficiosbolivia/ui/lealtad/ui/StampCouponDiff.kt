package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.StampCoupon

class StampCouponDiff  : DiffUtil.ItemCallback<StampCoupon>() {
    override fun areItemsTheSame(oldItem: StampCoupon, newItem: StampCoupon): Boolean {
        return oldItem.idCampana == newItem.idCampana
    }

    override fun areContentsTheSame(oldItem: StampCoupon, newItem: StampCoupon): Boolean {
        return oldItem == newItem
    }

}
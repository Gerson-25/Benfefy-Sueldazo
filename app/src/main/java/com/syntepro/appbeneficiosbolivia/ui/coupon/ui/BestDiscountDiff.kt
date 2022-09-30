package com.syntepro.appbeneficiosbolivia.ui.coupon.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.BestDiscountResponse

class BestDiscountDiff: DiffUtil.ItemCallback<BestDiscountResponse>() {
    override fun areItemsTheSame(oldItem: BestDiscountResponse, newItem: BestDiscountResponse): Boolean {
        return oldItem.idCoupon == newItem.idCoupon
    }

    override fun areContentsTheSame(oldItem: BestDiscountResponse, newItem: BestDiscountResponse): Boolean {
        return oldItem == newItem
    }

}
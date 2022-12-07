package com.appbenefy.sueldazo.ui.coupon.ui

import androidx.recyclerview.widget.DiffUtil
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountResponse

class BestDiscountDiff: DiffUtil.ItemCallback<BestDiscountResponse>() {
    override fun areItemsTheSame(oldItem: BestDiscountResponse, newItem: BestDiscountResponse): Boolean {
        return oldItem.idCampana == newItem.idCampana
    }

    override fun areContentsTheSame(oldItem: BestDiscountResponse, newItem: BestDiscountResponse): Boolean {
        return oldItem == newItem
    }

}
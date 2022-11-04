package com.syntepro.sueldazo.ui.coupon.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.sueldazo.ui.coupon.model.FeaturedCouponResponse

class FeaturedCouponDiff : DiffUtil.ItemCallback<FeaturedCouponResponse>() {
    override fun areItemsTheSame(oldItem: FeaturedCouponResponse, newItem: FeaturedCouponResponse): Boolean {
        return oldItem.idCoupon == newItem.idCoupon
    }

    override fun areContentsTheSame(oldItem: FeaturedCouponResponse, newItem: FeaturedCouponResponse): Boolean {
        return oldItem == newItem
    }

}
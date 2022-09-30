package com.syntepro.appbeneficiosbolivia.ui.coupon.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponListResponse

class CouponListDiff: DiffUtil.ItemCallback<CouponListResponse>() {
    override fun areItemsTheSame(oldItem: CouponListResponse, newItem: CouponListResponse): Boolean {
        return oldItem.idCoupon == newItem.idCoupon
    }

    override fun areContentsTheSame(oldItem: CouponListResponse, newItem: CouponListResponse): Boolean {
        return oldItem == newItem
    }

}
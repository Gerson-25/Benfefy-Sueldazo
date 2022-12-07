package com.appbenefy.sueldazo.ui.home.ui

import androidx.recyclerview.widget.DiffUtil
import com.appbenefy.sueldazo.ui.home.model.BannerResponse

class BannerDiff: DiffUtil.ItemCallback<BannerResponse>() {
    override fun areItemsTheSame(oldItem: BannerResponse, newItem: BannerResponse): Boolean {
        return oldItem.idBanner == newItem.idBanner
    }

    override fun areContentsTheSame(oldItem: BannerResponse, newItem: BannerResponse): Boolean {
        return oldItem == newItem
    }
}
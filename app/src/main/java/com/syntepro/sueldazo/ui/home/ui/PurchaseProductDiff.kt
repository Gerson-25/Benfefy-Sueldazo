package com.syntepro.sueldazo.ui.home.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.sueldazo.ui.home.model.PurchasedProductsResponse

class PurchaseProductDiff: DiffUtil.ItemCallback<PurchasedProductsResponse>() {
    override fun areItemsTheSame(oldItem: PurchasedProductsResponse, newItem: PurchasedProductsResponse): Boolean {
        return oldItem.idProduct == newItem.idProduct
    }

    override fun areContentsTheSame(oldItem: PurchasedProductsResponse, newItem: PurchasedProductsResponse): Boolean {
        return oldItem == newItem
    }
}
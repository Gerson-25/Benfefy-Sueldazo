package com.syntepro.appbeneficiosbolivia.ui.home.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.home.model.PurchasedProductsResponse

class PurchaseProductDiff: DiffUtil.ItemCallback<PurchasedProductsResponse>() {
    override fun areItemsTheSame(oldItem: PurchasedProductsResponse, newItem: PurchasedProductsResponse): Boolean {
        return oldItem.idProduct == newItem.idProduct
    }

    override fun areContentsTheSame(oldItem: PurchasedProductsResponse, newItem: PurchasedProductsResponse): Boolean {
        return oldItem == newItem
    }
}
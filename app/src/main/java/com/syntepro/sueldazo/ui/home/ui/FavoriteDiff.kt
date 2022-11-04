package com.syntepro.sueldazo.ui.home.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.sueldazo.ui.home.model.FavoriteResponse

class FavoriteDiff : DiffUtil.ItemCallback<FavoriteResponse>() {
    override fun areItemsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean {
        return oldItem.idFavorite == newItem.idFavorite
    }

    override fun areContentsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean {
        return oldItem == newItem
    }

}
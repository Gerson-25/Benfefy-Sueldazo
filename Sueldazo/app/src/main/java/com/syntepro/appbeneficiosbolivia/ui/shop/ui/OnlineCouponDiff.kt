package com.syntepro.appbeneficiosbolivia.ui.shop.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse

class OnlineCouponDiff: DiffUtil.ItemCallback<ArticleResponse>() {
    override fun areItemsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
        return oldItem.articleId == newItem.articleId
    }

    override fun areContentsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
        return oldItem.title == newItem.title
                && oldItem.title == newItem.title
    }
}
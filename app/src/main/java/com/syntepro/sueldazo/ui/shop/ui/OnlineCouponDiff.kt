package com.syntepro.sueldazo.ui.shop.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse

class OnlineCouponDiff: DiffUtil.ItemCallback<ArticleResponse>() {
    override fun areItemsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
        return oldItem.articleId == newItem.articleId
    }

    override fun areContentsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
        return oldItem.title == newItem.title
                && oldItem.title == newItem.title
    }
}
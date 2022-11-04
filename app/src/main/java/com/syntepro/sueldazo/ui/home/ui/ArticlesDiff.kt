package com.syntepro.sueldazo.ui.home.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse

class ArticlesDiff : DiffUtil.ItemCallback<ArticleResponse>() {
    override fun areItemsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
        return oldItem.articleId == newItem.articleId
    }

    override fun areContentsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
        return oldItem == newItem
    }

}
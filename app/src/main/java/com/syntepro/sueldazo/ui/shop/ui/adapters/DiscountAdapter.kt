package com.syntepro.sueldazo.ui.shop.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse
import com.syntepro.sueldazo.ui.shop.ui.OnlineCouponDiff
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.discount_item.view.*
import java.text.DecimalFormat

class DiscountAdapter(val adapterOnClick : (Any) -> Unit,
                      val favoriteOnClick: (Any, Int) -> Unit):
        PagedListAdapter<ArticleResponse, DiscountAdapter.ViewHolder>(OnlineCouponDiff()) {

    private val format = DecimalFormat("###,##0.0#")

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.discount_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
    ) {
        getItem(position)?.let { holder.bind(it, position) }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(model: ArticleResponse, position: Int) {
            Functions.showImage(model.imageUrl, itemView.couponImageId)
            itemView.titleCouponId.text = model.title

        }
    }
}
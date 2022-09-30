package com.syntepro.appbeneficiosbolivia.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.online_coupon_item.view.*
import java.text.DecimalFormat

class ItemAdapter(val adapterOnClick : (Any) -> Unit):
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var dataList = mutableListOf<ArticleResponse>()
    private val format = DecimalFormat("###,##0.0#")

    fun setListData(data: MutableList<ArticleResponse>) {
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.online_coupon_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (dataList.size > 0) dataList.size
        else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val provider = dataList[position]
        holder.bind(provider)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(model: ArticleResponse) {
            Functions.showImage(model.imageUrl, itemView.couponImageId)
            Functions.showRoundedImage(model.commerceImageUrl, itemView.commerceImageId)
            if (model.percentage == 0.0)itemView.couponTitleId.visibility = View.GONE else itemView.couponTitleId.text = "${format.format(model.percentage)}%"

            itemView.itemId.setOnClickListener { adapterOnClick(model) }
        }
    }

}
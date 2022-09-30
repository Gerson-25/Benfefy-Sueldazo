package com.syntepro.appbeneficiosbolivia.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryAdapter(val type: Int, val adapterOnClick : (Any) -> Unit):
        RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var dataList = mutableListOf<Category>()
    private var lastItem: Int = 0
    private lateinit var lastMaterialCard: MaterialCardView

    fun setListData(data: MutableList<Category>) {
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (type == 1) LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        else LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
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
        fun bind(model: Category) {
            Functions.showImage(model.urlImage, itemView.imageCategoryId)
            itemView.nameId.text = model.name

            itemView.itemId.setOnClickListener {
                adapterOnClick(model)
//                if(lastItem != adapterPosition) {
//                    uncheckCard(lastMaterialCard)
//                    markCurrent(itemView.itemId)
//                }
//                lastItem = adapterPosition
//                lastMaterialCard = itemView.itemId
            }

//            if(lastItem == adapterPosition) {
//                markCurrent(itemView.itemId)
//                lastMaterialCard = itemView.itemId
//            }
        }

        private fun markCurrent(card: MaterialCardView) {
            card.strokeWidth = 5
            card.strokeColor = ContextCompat.getColor(itemView.context, R.color.colorPrimary)
        }

        private fun uncheckCard(card: MaterialCardView) {
            card.strokeWidth = 0
        }

    }

}
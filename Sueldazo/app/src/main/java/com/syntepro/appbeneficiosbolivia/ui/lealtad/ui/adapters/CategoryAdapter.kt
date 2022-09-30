package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.AddPlanFragment
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.CategoryDiff
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.rv_category_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class CategoryAdapter @Inject constructor() :
        PagedListAdapter<Category, CategoryAdapter.ViewHolder>(
                CategoryDiff()
        ) {

    private var lastItem: Int = 0
    private lateinit var lastMaterialCard: MaterialCardView
    private var fragment: AddPlanFragment? = null

    fun parentFragment(fragment: AddPlanFragment) {
        this.fragment = fragment
    }

    internal var collection: List<Category> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: Category) {
            Functions.showImage(model.urlImage, view.imageId)
            view.nameId.text = model.name
            view.setOnClickListener {
                fragment?.getCommerce(model.idCategory!!)
//                if(lastItem != adapterPosition) {
//                    uncheckCard(lastMaterialCard)
//                    markCurrent(view.cardId)
//                }
//                lastItem = adapterPosition
//                lastMaterialCard = view.cardId
            }

//            if(lastItem == adapterPosition) {
//                markCurrent(view.cardId)
//                lastMaterialCard = view.cardId
//            }
        }

        private fun markCurrent(card: MaterialCardView) {
            card.strokeWidth = 5
            card.strokeColor = ContextCompat.getColor(view.context, R.color.colorPrimary)
        }

        private fun uncheckCard(card: MaterialCardView) {
            card.strokeWidth = 0
        }

    }

}
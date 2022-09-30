package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Commerce
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.CommerceDiff
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.AddPlanFragment
import kotlinx.android.synthetic.main.rv_category_item.view.imageId
import kotlinx.android.synthetic.main.rv_category_item.view.nameId
import kotlinx.android.synthetic.main.rv_commerce_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class CommerceAdapter @Inject constructor() :
        PagedListAdapter<Commerce, CommerceAdapter.ViewHolder>(
                CommerceDiff()
        ) {

    private var parentFragment: AddPlanFragment? = null
    internal var collection: List<Commerce> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size
    fun setFragment(fragment: AddPlanFragment) { parentFragment = fragment }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_commerce_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: Commerce) {
            Picasso.get().load(model.urlImage).into(view.imageId)
            view.nameId.text = model.nombre
            view.categoryId.text = model.categoryName
            view.setOnClickListener { parentFragment?.addPlan(model) }
        }


    }

}
package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanByUser
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.PlansItemDiffUtilCallback
import javax.inject.Inject
import kotlin.properties.Delegates

class PlansAdapter @Inject constructor() :
        PagedListAdapter<LoyaltyPlanByUser, PlansAdapter.ViewHolder>(
                PlansItemDiffUtilCallback()
        ) {

    internal var collection: List<LoyaltyPlanByUser> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_horario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: LoyaltyPlanByUser) {
           // view.descriptionId.text = model.descripcion
           // view.modelId.text = String.format(view.context.getString(R.string.model_label), model.modelo).fromHtml()
           // view.quantityId.text = String.format(view.context.getString(R.string.quantity_label), model.cantidad).fromHtml()
        }


    }

}
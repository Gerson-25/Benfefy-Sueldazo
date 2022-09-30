package com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.CommerceList2Activity
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Commerce
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.CommerceDiff
import kotlinx.android.synthetic.main.rv_commerce_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class CommerceListAdapter
@Inject constructor() :
        PagedListAdapter<Commerce, CommerceListAdapter.ViewHolder>(
                CommerceDiff()
        ) {

    private var activity: CommerceList2Activity? = null
    internal var collection: List<Commerce> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size
    fun setActivity(activity: CommerceList2Activity) { this.activity = activity }

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
            view.setOnClickListener { activity?.openDetail(model) }
        }
    }

}
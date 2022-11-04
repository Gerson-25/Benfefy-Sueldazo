package com.syntepro.sueldazo.ui.commerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.ui.commerce.model.CommerceDataModel
import com.syntepro.sueldazo.utils.Functions

class CommerceListAdapter (private var items: List<CommerceDataModel>,
                           val callback: CommerceListCallback)
    : RecyclerView.Adapter<CommerceListAdapter.BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            CommerceDataModel.HEADER -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.title_section_commerce, parent, false)
                return HeaderViewHolder(view)
            }
            CommerceDataModel.NORMAL -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.commerce_child_recycler, parent, false)
                return NormalViewHolder(view)
            }
            CommerceDataModel.FOOTER -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.footer_section_commerce, parent, false)
                return FooterViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = items[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(element,position)
            is NormalViewHolder -> holder.bind(element,position)
            is FooterViewHolder -> holder.bind(element,position)
            else -> throw IllegalArgumentException()
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, position: Int)
    }

    inner class HeaderViewHolder(itemView : View) : BaseViewHolder<CommerceDataModel>(itemView){
        // Rubro
        private val imageViewSection: ImageView = itemView.findViewById(R.id.imageViewSection)
        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val commerceCount: TextView = itemView.findViewById(R.id.commerceCount)
        private val lineSeparatorTitle: View = itemView.findViewById(R.id.lineSeparatorTitle)

        override fun bind(item: CommerceDataModel, position: Int) {
            if (position == 0) {
                lineSeparatorTitle.visibility = View.GONE
            } else {
                lineSeparatorTitle.visibility = View.VISIBLE
            }
            if (item.icono != null) Functions.showImage(item.icono, imageViewSection)
            else imageViewSection.setBackgroundResource(R.drawable.ic_info)
            itemName.text = item.nombre
            commerceCount.text = item.totalComercios.toString()
        }
    }

    inner class NormalViewHolder(itemView: View) : BaseViewHolder<CommerceDataModel>(itemView) {
        // Commerce
        private val imageId: ImageView = itemView.findViewById(R.id.imageId)
        private val nameId: TextView = itemView.findViewById(R.id.nameId)
        private val descriptionId: TextView = itemView.findViewById(R.id.descriptionId)
        private val childRowId: CardView = itemView.findViewById(R.id.childRowId)

        override fun bind(item: CommerceDataModel, position: Int) {
            Functions.showRoundedImage(item.comercio.imagen, imageId)
            nameId.text = item.comercio.nombre
            descriptionId.text = item.comercio.descripcion
            if(item.comercio.descripcion.isNullOrEmpty())
                descriptionId.visibility = View.GONE
            else
                descriptionId.visibility = View.VISIBLE

            childRowId.setOnClickListener { callback.openDetail(item.comercio.documentID!!, item.rubroName) }
        }
    }

    inner class FooterViewHolder(itemView: View) : BaseViewHolder<CommerceDataModel>(itemView) {
        private val linearVerMas: CardView = itemView.findViewById(R.id.linearVerMas)

        override fun bind(item: CommerceDataModel, position: Int) {
            linearVerMas.setOnClickListener { callback.viewCommerceList(item.rubroId,item.rubroName) }
        }
    }

    interface CommerceListCallback {
        fun viewCommerceList(rubroId: String, rubroName: String)
        fun openDetail(id: String, rubroName: String)
    }

}
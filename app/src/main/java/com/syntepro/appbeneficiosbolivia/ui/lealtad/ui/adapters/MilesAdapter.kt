package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.MilesCoupon
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.MilesDiff
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.MilesFragment
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.miles_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class MilesAdapter @Inject constructor() :
        PagedListAdapter<MilesCoupon, MilesAdapter.ViewHolder>(
                MilesDiff()
        ) {

    private var fragment: MilesFragment? = null

    fun parentFragment(fragment: MilesFragment) {
        this.fragment = fragment
    }

    internal var collection: List<MilesCoupon> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.miles_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: MilesCoupon) {
            if (model.disponible) {
                view.blockedView.visibility = View.GONE
                view.blockedImage.visibility = View.GONE
            } else {
                view.blockedView.visibility = View.VISIBLE
                view.blockedImage.visibility = View.VISIBLE
            }
            itemView.couponImageId.clipToOutline = true
            Functions.showImage(model.imagenCampana, itemView.couponImageId)
            view.titleCouponId.text = model.titulo
            view.subtitleCouponId.text = model.subtitulo
            view.milesId.text = "x${model.miles} millas"

            view.setOnClickListener { if (model.disponible) fragment?.openDetail(model.idCampana) }
        }
    }

}
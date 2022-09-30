package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.StampCoupon
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.StampCouponDiff
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.CardFragment
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.stampo_coupon_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class StampCouponAdapter@Inject constructor() :
        PagedListAdapter<StampCoupon, StampCouponAdapter.ViewHolder>(
                StampCouponDiff()
        ) {

    private var fragment: CardFragment? = null

    fun parentFragment(fragment: CardFragment) {
        this.fragment = fragment
    }

    internal var collection: List<StampCoupon> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stampo_coupon_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it, position) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: StampCoupon, position: Int) {
            if (model.disponible) {
                view.blockedView.visibility = View.GONE
                view.blockedImage.visibility = View.GONE
            } else {
                view.blockedView.visibility = View.VISIBLE
                view.blockedImage.visibility = View.VISIBLE
            }

            if (model.favorite) {
                val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
                itemView.favorite.startAnimation(animScale)
                itemView.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
            } else
                itemView.favorite.setBackgroundResource(R.drawable.ic_mark_fav)

            itemView.couponImageId.clipToOutline = true
            Functions.showImage(model.imagenCampana, itemView.couponImageId)
            itemView.titleCouponId.text = model.titulo
            itemView.subtitleCouponId.text = model.subtitulo
            itemView.sealsId.text = "${model.sellosRequeridos} ${view.context.getString(R.string.seal_label)}"

            view.setOnClickListener { if (model.disponible) fragment?.openDetail(model.idCampana) }
            view.favorite.setOnClickListener { fragment?.favorite(model, position) }
        }
    }

}
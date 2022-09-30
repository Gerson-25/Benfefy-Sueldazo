package com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.CorporateCoupon
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.CorporateDiff
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ui.fragments.CorporateFragment
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.corporate_item.view.*
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class CouponAdapter @Inject constructor() :
        PagedListAdapter<CorporateCoupon, CouponAdapter.ViewHolder>(
                CorporateDiff()
        ) {

    private var fragment: CorporateFragment? = null
    private val format = DecimalFormat("###,##0.0#")

    fun parentFragment(fragment: CorporateFragment) {
        this.fragment = fragment
    }

    internal var collection: List<CorporateCoupon> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.corporate_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it, position) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: CorporateCoupon, position: Int) {
            Functions.showImage(model.imagenCampana, itemView.couponImageId)
            itemView.titleCouponId.text = model.titulo
            itemView.subtitleCouponId.text = model.subtitulo
            when(model.tipo) {
                1 -> {
                    val newPrice = model.precioDescuento
                    itemView.couponOriginalPriceId.text = Html.fromHtml("<del>${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.precioReal)}</del>")
                    itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(newPrice)}"
                }
                2 -> {
                    val newPrice = model.precioDescuento
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = "${format.format(newPrice)}%"
                }
                3 -> {
                    val newPrice = model.precioDescuento
                    itemView.couponOriginalPriceId.text = ""
                    itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(newPrice)}"
                }
                else -> {
                    val newPrice = model.precioDescuento
                    itemView.couponOriginalPriceId.text = Html.fromHtml("<del>${Constants.userProfile?.currency ?: "Bs"} ${format.format(model.precioReal)}</del>")
                    itemView.couponPriceId.text = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(newPrice)}"
                }
            }

            if (model.favorite) {
                val animScale = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_fav)
                itemView.favorite.startAnimation(animScale)
                itemView.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
            } else
                itemView.favorite.setBackgroundResource(R.drawable.ic_mark_fav)

            view.setOnClickListener { fragment?.openDetail(model.idCampana) }
            view.favorite.setOnClickListener { fragment?.favorite(model, position) }
        }
    }

}
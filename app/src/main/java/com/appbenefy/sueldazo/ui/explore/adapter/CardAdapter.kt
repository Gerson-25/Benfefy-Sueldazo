package com.appbenefy.sueldazo.ui.explore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.entity.firebase.Cupon
import com.appbenefy.sueldazo.room.database.RoomDataBase
import com.appbenefy.sueldazo.utils.Functions
import com.appbenefy.sueldazo.utils.Functions.Companion.fromHtml
import kotlinx.android.synthetic.main.coupon_item.view.*
import java.util.*

class CardAdapter(private var mList: ArrayList<Cupon>, val adapterOnClick: (String) -> Unit)
    : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coupon_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.bind(mList[i], viewHolder.itemView.context)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong() //id()
    }

    inner class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(cardItem: Cupon, ctx: Context) {
            Functions.showImage(cardItem.imagenCupon, view.couponImageId)
            Functions.showRoundedImage(cardItem.imagenComercio, view.commerceImageId)
            view.titleCouponId.text = cardItem.titulo
            view.subtitleCouponId.text = cardItem.subtitulo
            view.couponOriginalPriceId.text = fromHtml("<strike>" + cardItem.precioReal + "</strike>")
            view.couponPriceId.text = cardItem.precioDesc

            view.favorite.setOnClickListener {
                val animScale = AnimationUtils.loadAnimation(ctx, R.anim.scale_fav)
                it.startAnimation(animScale)
            }

            view.setOnClickListener { adapterOnClick(cardItem.idCategoria) }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        RoomDataBase.destroyInstance()
    }

    fun setFilter(newCupon: ArrayList<Cupon>?) {
        mList = ArrayList()
        mList.addAll(newCupon!!)
        notifyDataSetChanged()
    }
}
package com.syntepro.appbeneficiosbolivia.ui.wallet

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.squareup.picasso.Picasso
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.firebase.SeguirComercio
import com.syntepro.appbeneficiosbolivia.utils.CircleTransform
import kotlinx.android.synthetic.main.wallet_row.view.*

class WalletAdapter(options: FirestorePagingOptions<SeguirComercio>, val ctx: Context)
    : FirestorePagingAdapter<SeguirComercio, WalletAdapter.ViewHolder>(options) {

    val colors = arrayListOf("#5E2129", "#EFB810", "#2D572C", "#008F39", "#2271B3", "#07685D","#636570","#848F07","#B40641","#9C9C9c")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.wallet_row, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: SeguirComercio) {
        holder.bind(model, currentList?.get(position)!!.id, position)
    }

    override fun onLoadingStateChanged(state: LoadingState) { }

    inner class ViewHolder (val view: View): RecyclerView.ViewHolder(view) {
        private val imageId = view.imageId
        private var nameId = view.nameId
        private var rowId: CardView = view.rowId
        private var milesId = view.milesId
        private var milesTitle = view.milesTitle

        fun bind(model: SeguirComercio, id: String, position: Int) {
            val pos = position.rem(10)
            if (model.colorTarjeta != null) {
                // Rodrigo Osegueda 04JUN2020 - The color of the card is modified
                rowId.setCardBackgroundColor(Color.parseColor(model.colorTarjeta))
            } else {
                rowId.setCardBackgroundColor(Color.parseColor(colors[pos]))
            }
            showRoundedImage(model.logo, imageId)
            nameId.text = model.nombre
            if (model.permiteMillas!!) {
                milesId.text = model.millas.toString()
            } else {
                milesId.visibility = View.INVISIBLE
                milesTitle.visibility = View.INVISIBLE
            }
        }

        private fun showRoundedImage(image:String?, imageView: ImageView) {
            if(image.isNullOrEmpty()) return
            Picasso.get().load(image).transform(CircleTransform()).into(imageView)
        }
    }




}
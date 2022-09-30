package com.syntepro.appbeneficiosbolivia.ui.menu.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.entity.firebase.Comentario
import kotlinx.android.synthetic.main.rv_comentarios_items.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter(private val mList: ArrayList<Comentario>)
    : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_comentarios_items, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.bind(mList[i])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(model: Comentario) {
            if (model.fechaComentario != null) {
                val date = Date(model.fechaComentario?.toDate().toString())
                @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("dd/MM/yyyy h:mm aa ")
                val format = formatter.format(date)
                itemView.fecha.text = format
            }
            itemView.comentario.text = model.comentario
        }
    }
}
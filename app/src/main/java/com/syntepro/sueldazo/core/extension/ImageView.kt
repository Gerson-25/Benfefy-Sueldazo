package com.merckers.asesorcajero.core.extension

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.squareup.picasso.Picasso

fun ImageView.showImage(imageName: String?, thumbImage: String?, pg: ProgressBar?) {
    if(imageName.isNullOrEmpty()) return

        pg?.visibility = if(thumbImage != null) View.VISIBLE else View.GONE

    Picasso.get()
        .load(imageName)
        .placeholder(this.drawable)
        .into(this, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                pg?.visibility = View.GONE
            }
            override fun onError(e: Exception) {}
        })
}
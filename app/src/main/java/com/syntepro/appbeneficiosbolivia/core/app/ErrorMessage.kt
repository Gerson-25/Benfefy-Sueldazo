package com.syntepro.appbeneficiosbolivia.core.app

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.syntepro.appbeneficiosbolivia.R

object ErrorMessage {
    internal fun notifyWithAction(
            view: View,
            message: String,
            @StringRes actionText: Int,
            action: () -> Any
    ) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        val color = ContextCompat.getColor(
                view.context,
                R.color.white
        )
        snackBar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.design_default_color_error))
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                .setTextColor(color)
        snackBar.setAction(actionText) { action.invoke() }
        snackBar.setActionTextColor(ContextCompat.getColor(
                view.context,
                R.color.white
        ))
        snackBar.show()
    }
}
package com.syntepro.appbeneficiosbolivia.core.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.LinearLayout


class MerckersRadioGroup : LinearLayout {
    private val mCheckables = ArrayList<View>()

    constructor(context: Context?) : super(context) {}

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int = 0) : super(
        context,
        attrs,
        defStyle
    )

    override fun addView(
        child: View, index: Int,
        params: ViewGroup.LayoutParams
    ) {
        super.addView(child, index, params)
        parseChild(child)
    }

    private fun parseChild(child: View) {
        if (child is Checkable) {
            mCheckables.add(child)
            child.setOnClickListener { v ->
                for (i in 0 until mCheckables.size) {
                    val view = mCheckables[i] as Checkable
                    if (view === v) {
                        (view as Checkable).isChecked = true
                    } else {
                        view.isChecked = false
                    }
                }
            }
        } else if (child is ViewGroup) {
            parseChildren(child)
        }
    }

    private fun parseChildren(child: ViewGroup) {
        for (i in 0 until child.childCount) {
            parseChild(child.getChildAt(i))
        }
    }
}
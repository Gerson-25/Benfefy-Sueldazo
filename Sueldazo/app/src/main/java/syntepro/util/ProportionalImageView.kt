package syntepro.util

import android.content.Context
import android.util.AttributeSet

class ProportionalImageView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable
        if (d != null) {
            val w = MeasureSpec.getSize(widthMeasureSpec)
            val h =   MeasureSpec.getSize(heightMeasureSpec)//w * d.intrinsicHeight / d.intrinsicWidth
            setMeasuredDimension(w, h)
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
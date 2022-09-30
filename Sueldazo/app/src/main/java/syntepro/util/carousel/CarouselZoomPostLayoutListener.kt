package syntepro.util.carousel

import android.view.View



/**
 * Implementation of [CarouselLayoutManager.PostLayoutListener] that makes interesting scaling of items. <br></br>
 * We are trying to make items scaling quicker for closer items for center and slower for when they are far away.<br></br>
 * Tis implementation uses atan function for this purpose.
 */
class CarouselZoomPostLayoutListener : CarouselLayoutManager.PostLayoutListener {
    override fun transformChild(child: View, itemPositionToCenterDiff: Float, orientation: Int): ItemTransformation {
        val scale = (2 * (2 * -StrictMath.atan(Math.abs(itemPositionToCenterDiff) + 1.0) / Math.PI + 1)).toFloat()
        // because scaling will make view smaller in its center, then we should move this item to the top or bottom to make it visible
        val translateY: Float
        val translateX: Float
        if (CarouselLayoutManager.VERTICAL == orientation) {
            val translateYGeneral: Float = child.getMeasuredHeight() * (1 - scale) / 2f
            translateY = Math.signum(itemPositionToCenterDiff) * translateYGeneral
            translateX = 0f
        } else {
            val translateXGeneral: Float = child.getMeasuredWidth() * (1 - scale) / 2f
            translateX = Math.signum(itemPositionToCenterDiff) * translateXGeneral
            translateY = 0f
        }
        return ItemTransformation(scale, scale, translateX, translateY)
    }
}
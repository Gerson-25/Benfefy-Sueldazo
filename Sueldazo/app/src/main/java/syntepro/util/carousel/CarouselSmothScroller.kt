package syntepro.util.carousel

import android.graphics.PointF
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * Custom implementation of [android.support.v7.widget.RecyclerView.SmoothScroller] that can work only with [CarouselLayoutManager].
 *
 * @see CarouselLayoutManager
 */
class CarouselSmoothScroller(state: RecyclerView.State, position: Int) {
    fun computeScrollVectorForPosition(targetPosition: Int, carouselLayoutManager: CarouselLayoutManager): PointF? {
        return carouselLayoutManager.computeScrollVectorForPosition(targetPosition)
    }

    fun calculateDyToMakeVisible(view: View?, carouselLayoutManager: CarouselLayoutManager): Int {
        return if (!carouselLayoutManager.canScrollVertically()) {
            0
        } else carouselLayoutManager.getOffsetForCurrentView(view!!)
    }

    fun calculateDxToMakeVisible(view: View?, carouselLayoutManager: CarouselLayoutManager): Int {
        return if (!carouselLayoutManager.canScrollHorizontally()) {
            0
        } else carouselLayoutManager.getOffsetForCurrentView(view!!)
    }

    init {
        require(0 <= position) { "position can't be less then 0. position is : $position" }
        require(position < state.itemCount) { "position can't be great then adapter items count. position is : $position" }
    }
}
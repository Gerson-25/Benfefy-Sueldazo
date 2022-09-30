package syntepro.util.carousel

import androidx.recyclerview.widget.RecyclerView


/**
 * Class for centering items after scroll event.<br></br>
 * This class will listen to current scroll state and if item is not centered after scroll it will automatically scroll it to center.
 */
class CenterScrollListener : RecyclerView.OnScrollListener() {
    private var mAutoSet = true
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager !is CarouselLayoutManager) {
            mAutoSet = true
            return
        }
        if (!mAutoSet) {
            if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                layoutManager.setItemCenterStopped(layoutManager.getCenterItemPosition())
                val scrollNeeded = layoutManager.getOffsetCenterView()
                if (CarouselLayoutManager.HORIZONTAL == layoutManager.getOrientation()) {
                    recyclerView.smoothScrollBy(scrollNeeded, 0)
                } else {
                    recyclerView.smoothScrollBy(0, scrollNeeded)
                }
                mAutoSet = true
            }
        }
        if (RecyclerView.SCROLL_STATE_DRAGGING == newState || RecyclerView.SCROLL_STATE_SETTLING == newState) {
            mAutoSet = false
        }
    }
}
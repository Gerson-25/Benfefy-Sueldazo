package syntepro.util.carousel

import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.Nullable
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import java.lang.ref.WeakReference
import java.util.*


/**
 * An implementation of [RecyclerView.LayoutManager] that layout items like carousel.
 * Generally there is one center item and bellow this item there are maximum [CarouselLayoutManager.getMaxVisibleItems] items on each side of the center
 * item. By default [CarouselLayoutManager.getMaxVisibleItems] is [CarouselLayoutManager.MAX_VISIBLE_ITEMS].<br></br>
 * <br></br>
 * This LayoutManager supports only fixedSized adapter items.<br></br>
 * <br></br>
 * This LayoutManager supports [CarouselLayoutManager.HORIZONTAL] and [CarouselLayoutManager.VERTICAL] orientations. <br></br>
 * <br></br>
 * This LayoutManager supports circle layout. By default it if disabled. We don't recommend to use circle layout with adapter items count less then 3. <br></br>
 * <br></br>
 * Please be sure that layout_width of adapter item is a constant value and not [ViewGroup.LayoutParams.MATCH_PARENT]
 * for [.HORIZONTAL] orientation.
 * So like layout_height is not [ViewGroup.LayoutParams.MATCH_PARENT] for [CarouselLayoutManager.VERTICAL]<br></br>
 * <br></br>
 */
class CarouselLayoutManager @JvmOverloads constructor(orientation: Int, circleLayout: Boolean = CIRCLE_LAYOUT) : RecyclerView.LayoutManager(), ScrollVectorProvider {
    private var mDecoratedChildSizeInvalid = false
    private var mDecoratedChildWidth: Int? = null
    private var mDecoratedChildHeight: Int? = null
    private val mOrientation: Int
    private val mCircleLayout: Boolean
    private var mPendingScrollPosition: Int
    private val mLayoutHelper = LayoutHelper(MAX_VISIBLE_ITEMS)
    private var mViewPostLayout: PostLayoutListener? = null
    private val mOnCenterItemSelectionListeners: MutableList<OnCenterItemSelectionListener> = ArrayList()
    private var mCenterItemPosition = INVALID_POSITION
    private var mItemsCount = 0
    @Nullable
    private var mPendingCarouselSavedState: CarouselSavedState? = null

    /**
     * Setup [CarouselLayoutManager.PostLayoutListener] for this LayoutManager.
     * Its methods will be called for each visible view item after general LayoutManager layout finishes. <br></br>
     * <br></br>
     * Generally this method should be used for scaling and translating view item for better (different) view presentation of layouting.
     *
     * @param postLayoutListener listener for item layout changes. Can be null.
     */
    fun setPostLayoutListener(@Nullable postLayoutListener: PostLayoutListener?) {
        mViewPostLayout = postLayoutListener
        requestLayout()
    }

    /**
     * Setup maximum visible (layout) items on each side of the center item.
     * Basically during scrolling there can be more visible items (+1 item on each side), but in idle state this is the only reached maximum.
     *
     * @param maxVisibleItems should be great then 0, if bot an [IllegalAccessException] will be thrown
     */
    @CallSuper
    fun setMaxVisibleItems(maxVisibleItems: Int?) {
        require(0 < maxVisibleItems!!) { "maxVisibleItems can't be less then 1" }
        mLayoutHelper.mMaxVisibleItems = maxVisibleItems
        requestLayout()
    }

    var maxVisibleItems: Int = 0
        get() = mLayoutHelper.mMaxVisibleItems

    /**
     * @return current setup for maximum visible items.
     * @see .setMaxVisibleItems
     */
    //fun getMaxVisibleItems(): Int {
    //    return mLayoutHelper.mMaxVisibleItems
   // }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * @return current layout orientation
     * @see .VERTICAL
     *
     * @see .HORIZONTAL
     */
    fun getOrientation(): Int {
        return mOrientation
    }

    override fun canScrollHorizontally(): Boolean {
        return 0 != childCount && HORIZONTAL == mOrientation
    }

    override fun canScrollVertically(): Boolean {
        return 0 != childCount && VERTICAL == mOrientation
    }

    /**
     * @return current layout center item
     */
    fun getCenterItemPosition(): Int {
        return mCenterItemPosition
    }

    /**
     * @param onCenterItemSelectionListener listener that will trigger when ItemSelectionChanges. can't be null
     */
    fun addOnItemSelectionListener(onCenterItemSelectionListener: OnCenterItemSelectionListener) {
        mOnCenterItemSelectionListeners.add(onCenterItemSelectionListener)
    }

    /**
     * @param onCenterItemSelectionListener listener that was previously added by [.addOnItemSelectionListener]
     */
    fun removeOnItemSelectionListener(onCenterItemSelectionListener: OnCenterItemSelectionListener) {
        mOnCenterItemSelectionListeners.remove(onCenterItemSelectionListener)
    }

    override fun scrollToPosition(position: Int) {
        require(0 <= position) { "position can't be less then 0. position is : $position" }
        mPendingScrollPosition = position
        requestLayout()
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateDyToMakeVisible(view: View, snapPreference: Int): Int {
                return if (!canScrollVertically()) {
                    0
                } else getOffsetForCurrentView(view)
            }

            override fun calculateDxToMakeVisible(view: View, snapPreference: Int): Int {
                return if (!canScrollHorizontally()) {
                    0
                } else getOffsetForCurrentView(view)
            }
        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    @Nullable
    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (0 == childCount) {
            return null
        }
        val directionDistance = getScrollDirection(targetPosition)
        val direction = (-Math.signum(directionDistance)).toInt()
        return if (HORIZONTAL == mOrientation) {
            PointF(direction.toFloat(), 0f)
        } else {
            PointF(0f, direction.toFloat())
        }
    }

    private fun getScrollDirection(targetPosition: Int): Float {
        val currentScrollPosition = makeScrollPositionInRange0ToCount(getCurrentScrollPosition(), mItemsCount)
        return if (mCircleLayout) {
            val t1 = currentScrollPosition - targetPosition
            val t2 = Math.abs(t1) - mItemsCount
            if (Math.abs(t1) > Math.abs(t2)) {
                Math.signum(t1) * t2
            } else {
                t1
            }
        } else {
            currentScrollPosition - targetPosition
        }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        return if (HORIZONTAL == mOrientation) {
            0
        } else scrollBy(dy, recycler, state)
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        return if (VERTICAL == mOrientation) {
            0
        } else scrollBy(dx, recycler, state)
    }

    /**
     * This method is called from [.scrollHorizontallyBy] and
     * [.scrollVerticallyBy] to calculate needed scroll that is allowed. <br></br>
     * <br></br>
     * This method may do relayout work.
     *
     * @param diff     distance that we want to scroll by
     * @param recycler Recycler to use for fetching potentially cached views for a position
     * @param state    Transient state of RecyclerView
     * @return distance that we actually scrolled by
     */
    @CallSuper
    protected fun scrollBy(diff: Int, recycler: Recycler, state: RecyclerView.State): Int {
        if (null == mDecoratedChildWidth || null == mDecoratedChildHeight) {
            return 0
        }
        if (0 == childCount || 0 == diff) {
            return 0
        }
        val resultScroll: Int
        if (mCircleLayout) {
            resultScroll = diff
            mLayoutHelper.mScrollOffset += resultScroll
            val maxOffset = getScrollItemSize() * mItemsCount
            while (0 > mLayoutHelper.mScrollOffset) {
                mLayoutHelper.mScrollOffset += maxOffset
            }
            while (mLayoutHelper.mScrollOffset > maxOffset) {
                mLayoutHelper.mScrollOffset -= maxOffset
            }
            mLayoutHelper.mScrollOffset -= resultScroll
        } else {
            val maxOffset = getMaxScrollOffset()
            resultScroll = if (0 > mLayoutHelper.mScrollOffset + diff) {
                -mLayoutHelper.mScrollOffset //to make it 0
            } else if (mLayoutHelper.mScrollOffset + diff > maxOffset) {
                maxOffset - mLayoutHelper.mScrollOffset //to make it maxOffset
            } else {
                diff
            }
        }
        if (0 != resultScroll) {
            mLayoutHelper.mScrollOffset += resultScroll
            fillData(recycler, state)
        }
        return resultScroll
    }

    override fun onMeasure(recycler: Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        mDecoratedChildSizeInvalid = true
        super.onMeasure(recycler, state, widthSpec, heightSpec)
    }

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        removeAllViews()
    }

    @CallSuper
    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        if (0 == state.itemCount) {
            removeAndRecycleAllViews(recycler)
            selectItemCenterPosition(INVALID_POSITION)
            return
        }
        if (null == mDecoratedChildWidth || mDecoratedChildSizeInvalid) {
            val view = recycler.getViewForPosition(0)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val decoratedChildWidth = getDecoratedMeasuredWidth(view)
            val decoratedChildHeight = getDecoratedMeasuredHeight(view)
            removeAndRecycleView(view, recycler)
            if (null != mDecoratedChildWidth && (mDecoratedChildWidth != decoratedChildWidth || mDecoratedChildHeight != decoratedChildHeight)) {
                if (INVALID_POSITION == mPendingScrollPosition && null == mPendingCarouselSavedState) {
                    mPendingScrollPosition = mCenterItemPosition
                }
            }
            mDecoratedChildWidth = decoratedChildWidth
            mDecoratedChildHeight = decoratedChildHeight
            mDecoratedChildSizeInvalid = false
        }
        if (INVALID_POSITION != mPendingScrollPosition) {
            val itemsCount = state.itemCount
            mPendingScrollPosition = if (0 == itemsCount) INVALID_POSITION else Math.max(0, Math.min(itemsCount - 1, mPendingScrollPosition))
        }
        if (INVALID_POSITION != mPendingScrollPosition) {
            mLayoutHelper.mScrollOffset = calculateScrollForSelectingPosition(mPendingScrollPosition, state)
            mPendingScrollPosition = INVALID_POSITION
            mPendingCarouselSavedState = null
        } else if (null != mPendingCarouselSavedState) {
            mLayoutHelper.mScrollOffset = calculateScrollForSelectingPosition(mPendingCarouselSavedState!!.mCenterItemPosition, state)
            mPendingCarouselSavedState = null
        } else if (state.didStructureChange() && INVALID_POSITION != mCenterItemPosition) {
            mLayoutHelper.mScrollOffset = calculateScrollForSelectingPosition(mCenterItemPosition, state)
        }
        fillData(recycler, state)
    }

    private fun calculateScrollForSelectingPosition(itemPosition: Int, state: RecyclerView.State): Int {
        val fixedItemPosition = if (itemPosition < state.itemCount) itemPosition else state.itemCount - 1
        return fixedItemPosition * if (VERTICAL == mOrientation) mDecoratedChildHeight!! else mDecoratedChildWidth!!
    }

    private fun fillData(recycler: Recycler, state: RecyclerView.State) {
        val currentScrollPosition = getCurrentScrollPosition()
        generateLayoutOrder(currentScrollPosition, state)
        detachAndScrapAttachedViews(recycler)
        recyclerOldViews(recycler)
        val width = getWidthNoPadding()
        val height = getHeightNoPadding()
        if (VERTICAL == mOrientation) {
            fillDataVertical(recycler, width, height)
        } else {
            fillDataHorizontal(recycler, width, height)
        }
        recycler.clear()
        detectOnItemSelectionChanged(currentScrollPosition, state)
    }

    private fun detectOnItemSelectionChanged(currentScrollPosition: Float, state: RecyclerView.State) {
        val absCurrentScrollPosition = makeScrollPositionInRange0ToCount(currentScrollPosition, state.itemCount)
        val centerItem = Math.round(absCurrentScrollPosition)
        if (mCenterItemPosition != centerItem) {
            mCenterItemPosition = centerItem
            Handler(Looper.getMainLooper()).post { selectItemCenterPosition(centerItem) }
        }
    }

    private fun selectItemCenterPosition(centerItem: Int) {
        for (onCenterItemSelectionListener in mOnCenterItemSelectionListeners) {
            onCenterItemSelectionListener.onCenterItemChanged(centerItem)
        }
    }

    // Abel Acosta
    fun setItemCenterStopped(centerItem: Int) {
        for (onCenterItemSelectionListener in mOnCenterItemSelectionListeners) {
            onCenterItemSelectionListener.onCenterItemChangedStopped(centerItem)
        }
    }


    private fun fillDataVertical(recycler: Recycler, width: Int, height: Int) {
        val start = (width - mDecoratedChildWidth!!) / 2
        val end = start + mDecoratedChildWidth!!
        val centerViewTop = (height - mDecoratedChildHeight!!) / 2
        var i = 0
        val count = mLayoutHelper.mLayoutOrder!!.size
        while (i < count) {
            val layoutOrder = mLayoutHelper.mLayoutOrder!![i]
            val offset = getCardOffsetByPositionDiff(layoutOrder!!.mItemPositionDiff)
            val top = centerViewTop + offset
            val bottom = top + mDecoratedChildHeight!!
            fillChildItem(start, top, end, bottom, layoutOrder, recycler, i)
            ++i
        }
    }

    private fun fillDataHorizontal(recycler: Recycler, width: Int, height: Int) {
        val top = (height - mDecoratedChildHeight!!) / 2
        val bottom = top + mDecoratedChildHeight!!
        val centerViewStart = (width - mDecoratedChildWidth!!) / 2
        var i = 0
        val count = mLayoutHelper.mLayoutOrder!!.size
        while (i < count) {
            val layoutOrder = mLayoutHelper.mLayoutOrder!![i]
            val offset = getCardOffsetByPositionDiff(layoutOrder!!.mItemPositionDiff)
            val start = centerViewStart + offset
            val end = start + mDecoratedChildWidth!!
            fillChildItem(start, top, end, bottom, layoutOrder, recycler, i)
            ++i
        }
    }

    private fun fillChildItem(start: Int, top: Int, end: Int, bottom: Int, layoutOrder: LayoutOrder, recycler: Recycler, i: Int) {
        val view = bindChild(layoutOrder.mItemAdapterPosition, recycler)
        ViewCompat.setElevation(view, i.toFloat())
        var transformation: ItemTransformation? = null
        if (null != mViewPostLayout) {
            transformation = mViewPostLayout!!.transformChild(view, layoutOrder.mItemPositionDiff, mOrientation)
        }
        if (null == transformation) {
            view.layout(start, top, end, bottom)
        } else {
            view.layout(Math.round(start + transformation.mTranslationX), Math.round(top + transformation.mTranslationY),
                    Math.round(end + transformation.mTranslationX), Math.round(bottom + transformation.mTranslationY))
            view.scaleX = transformation.mScaleX
            view.scaleY = transformation.mScaleY
            //if(mLayoutHelper.mLayoutOrder!!.get(i)?.mItemAdapterPosition != getCenterItemPosition())
            //    view.alpha = 0.5f
            //else
           //     view.alpha = 1.0f
        }
    }

    /**
     * @return current scroll position of center item. this value can be in any range if it is cycle layout.
     * if this is not, that then it is in [0, [- 1][.mItemsCount]]
     */
    private fun getCurrentScrollPosition(): Float {
        val fullScrollSize = getMaxScrollOffset()
        return if (0 == fullScrollSize) {
            0f
        } else 1.0f * mLayoutHelper.mScrollOffset / getScrollItemSize()
    }

    /**
     * @return maximum scroll value to fill up all items in layout. Generally this is only needed for non cycle layouts.
     */
    private fun getMaxScrollOffset(): Int {
        return getScrollItemSize() * (mItemsCount - 1)
    }

    /**
     * Because we can support old Android versions, we should layout our children in specific order to make our center view in the top of layout
     * (this item should layout last). So this method will calculate layout order and fill up [.mLayoutHelper] object.
     * This object will be filled by only needed to layout items. Non visible items will not be there.
     *
     * @param currentScrollPosition current scroll position this is a value that indicates position of center item
     * (if this value is int, then center item is really in the center of the layout, else it is near state).
     * Be aware that this value can be in any range is it is cycle layout
     * @param state                 Transient state of RecyclerView
     * @see .getCurrentScrollPosition
     */
    private fun generateLayoutOrder(currentScrollPosition: Float, state: RecyclerView.State) {
        // Abel Acosta
        if(mItemsCount == 0 && state.itemCount > 0)
            setItemCenterStopped(0)
        mItemsCount = state.itemCount
        val absCurrentScrollPosition = makeScrollPositionInRange0ToCount(currentScrollPosition, mItemsCount)
        val centerItem = Math.round(absCurrentScrollPosition)
        if (mCircleLayout && 1 < mItemsCount) {
            val layoutCount = Math.min(mLayoutHelper.mMaxVisibleItems * 2 + 3, mItemsCount) // + 3 = 1 (center item) + 2 (addition bellow maxVisibleItems)
            mLayoutHelper.initLayoutOrder(layoutCount)
            val countLayoutHalf = layoutCount / 2
            // before center item
            for (i in 1..countLayoutHalf) {
                val position = Math.round(absCurrentScrollPosition - i + mItemsCount) % mItemsCount
                mLayoutHelper.setLayoutOrder(countLayoutHalf - i, position, centerItem - absCurrentScrollPosition - i)
            }
            // after center item
            for (i in layoutCount - 1 downTo countLayoutHalf + 1) {
                val position = Math.round(absCurrentScrollPosition - i + layoutCount) % mItemsCount
                mLayoutHelper.setLayoutOrder(i - 1, position, centerItem - absCurrentScrollPosition + layoutCount - i)
            }
            mLayoutHelper.setLayoutOrder(layoutCount - 1, centerItem, centerItem - absCurrentScrollPosition)
        } else {
            val firstVisible = Math.max(centerItem - mLayoutHelper.mMaxVisibleItems - 1, 0)
            val lastVisible = Math.min(centerItem + mLayoutHelper.mMaxVisibleItems + 1, mItemsCount - 1)
            val layoutCount = lastVisible - firstVisible + 1
            mLayoutHelper.initLayoutOrder(layoutCount)
            for (i in firstVisible..lastVisible) {
                if (i == centerItem) {
                    mLayoutHelper.setLayoutOrder(layoutCount - 1, i, i - absCurrentScrollPosition)
                } else if (i < centerItem) {
                    mLayoutHelper.setLayoutOrder(i - firstVisible, i, i - absCurrentScrollPosition)
                } else {
                    mLayoutHelper.setLayoutOrder(layoutCount - (i - centerItem) - 1, i, i - absCurrentScrollPosition)
                }
            }
        }
    }

    fun getWidthNoPadding(): Int {
        return width - paddingStart - paddingEnd
    }

    fun getHeightNoPadding(): Int {
        return height - paddingEnd - paddingStart
    }

    private fun bindChild(position: Int, recycler: Recycler): View {
        val view = recycler.getViewForPosition(position)
        addView(view)
        measureChildWithMargins(view, 0, 0)
        return view
    }

    private fun recyclerOldViews(recycler: Recycler) {
        for (viewHolder in ArrayList(recycler.scrapList)) {
            val adapterPosition = viewHolder.adapterPosition
            var found = false
            for (layoutOrder in mLayoutHelper.mLayoutOrder!!) {
                if (layoutOrder!!.mItemAdapterPosition == adapterPosition) {
                    found = true
                    break
                }
            }
            if (!found) {
                recycler.recycleView(viewHolder.itemView)
            }
        }
    }

    /**
     * Called during [.fillData] to calculate item offset from layout center line. <br></br>
     * <br></br>
     * Returns [.convertItemPositionDiffToSmoothPositionDiff] * (size off area above center item when it is on the center). <br></br>
     * Sign is: plus if this item is bellow center line, minus if not<br></br>
     * <br></br>
     * ----- - area above it<br></br>
     * ||||| - center item<br></br>
     * ----- - area bellow it (it has the same size as are above center item)<br></br>
     *
     * @param itemPositionDiff current item difference with layout center line. if this is 0, then this item center is in layout center line.
     * if this is 1 then this item is bellow the layout center line in the full item size distance.
     * @return offset in scroll px coordinates.
     */
    protected fun getCardOffsetByPositionDiff(itemPositionDiff: Float): Int {
        val smoothPosition = convertItemPositionDiffToSmoothPositionDiff(itemPositionDiff)
        val dimenDiff: Int
        dimenDiff = if (VERTICAL == mOrientation) {
            (getHeightNoPadding() - mDecoratedChildHeight!!) / 2
        } else {
            (getWidthNoPadding() - mDecoratedChildWidth!!) / 2
        }
        return Math.round(Math.signum(itemPositionDiff) * dimenDiff * smoothPosition).toInt()
    }

    /**
     * Called during [.getCardOffsetByPositionDiff] for better item movement. <br></br>
     * Current implementation speed up items that are far from layout center line and slow down items that are close to this line.
     * This code is full of maths. If you want to make items move in a different way, probably you should override this method.<br></br>
     * Please see code comments for better explanations.
     *
     * @param itemPositionDiff current item difference with layout center line. if this is 0, then this item center is in layout center line.
     * if this is 1 then this item is bellow the layout center line in the full item size distance.
     * @return smooth position offset. needed for scroll calculation and better user experience.
     * @see .getCardOffsetByPositionDiff
     */
    protected fun convertItemPositionDiffToSmoothPositionDiff(itemPositionDiff: Float): Double { // generally item moves the same way above center and bellow it. So we don't care about diff sign.
        val absIemPositionDiff = Math.abs(itemPositionDiff)
        // we detect if this item is close for center or not. We use (1 / maxVisibleItem) ^ (1/3) as close definer.
        return if (absIemPositionDiff > StrictMath.pow(1.0f / mLayoutHelper.mMaxVisibleItems.toDouble(), 1.0f / 3.toDouble())) { // this item is far from center line, so we should make it move like square root function
            StrictMath.pow(absIemPositionDiff / mLayoutHelper.mMaxVisibleItems.toDouble(), 1 / 2.0f.toDouble())
        } else { // this item is close from center line. we should slow it down and don't make it speed up very quick.
// so square function in range of [0, (1/maxVisible)^(1/3)] is quite good in it;
            StrictMath.pow(absIemPositionDiff.toDouble(), 2.0)
        }
    }

    /**
     * @return full item size
     */
    protected fun getScrollItemSize(): Int {
        return if (VERTICAL == mOrientation) {
            mDecoratedChildHeight!!
        } else {
            mDecoratedChildWidth!!
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        if (null != mPendingCarouselSavedState) {
            return CarouselSavedState(mPendingCarouselSavedState!!)
        }
        val savedState = CarouselSavedState(super.onSaveInstanceState())
        savedState.mCenterItemPosition = mCenterItemPosition
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is CarouselSavedState) {
            mPendingCarouselSavedState = state
            super.onRestoreInstanceState(mPendingCarouselSavedState!!.mSuperState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * @return Scroll offset from nearest item from center
     */
    fun getOffsetCenterView(): Int {
        return Math.round(getCurrentScrollPosition()) * getScrollItemSize() - mLayoutHelper.mScrollOffset
    }

    fun getOffsetForCurrentView(view: View): Int {
        val targetPosition = getPosition(view)
        val directionDistance = getScrollDirection(targetPosition)
        val distance = Math.round(directionDistance * getScrollItemSize())
        return if (mCircleLayout) {
            distance
        } else {
            distance
        }
    }

    /**
     * This interface methods will be called for each visible view item after general LayoutManager layout finishes. <br></br>
     * <br></br>
     * Generally this method should be used for scaling and translating view item for better (different) view presentation of layouting.
     */
    interface PostLayoutListener {
        /**
         * Called after child layout finished. Generally you can do any translation and scaling work here.
         *
         * @param child                    view that was layout
         * @param itemPositionToCenterDiff view center line difference to layout center. if > 0 then this item is bellow layout center line, else if not
         * @param orientation              layoutManager orientation [.getLayoutDirection]
         */
        fun transformChild(child: View, itemPositionToCenterDiff: Float, orientation: Int): ItemTransformation?
    }

    interface OnCenterItemSelectionListener {
        /**
         * Listener that will be called on every change of center item.
         * This listener will be triggered on **every** layout operation if item was changed.
         * Do not do any expensive operations in this method since this will effect scroll experience.
         *
         * @param adapterPosition current layout center item
         */
        fun onCenterItemChanged(adapterPosition: Int)
        fun onCenterItemChangedStopped(adapterPosition: Int)
    }

    /**
     * Helper class that holds currently visible items.
     * Generally this class fills this list. <br></br>
     * <br></br>
     * This class holds all scroll and maxVisible items state.
     *
     * @see .getMaxVisibleItems
     */
    private class LayoutHelper internal constructor( var mMaxVisibleItems: Int) {
        var mScrollOffset = 0
        var mLayoutOrder: Array<LayoutOrder?>? = emptyArray()
        private val mReusedItems: MutableList<WeakReference<LayoutOrder>> = ArrayList()
        /**
         * Called before any fill calls. Needed to recycle old items and init new array list. Generally this list is an array an it is reused.
         *
         * @param layoutCount items count that will be layout
         */
        fun initLayoutOrder(layoutCount: Int) {
            if (null == mLayoutOrder || mLayoutOrder!!.size != layoutCount) {
                if (null != mLayoutOrder) {
                    recycleItems(*mLayoutOrder!!)
                }

                mLayoutOrder = arrayOfNulls<LayoutOrder?>(layoutCount)
                fillLayoutOrder()
            }
        }

        /**
         * Called during layout generation process of filling this list. Should be called only after [.initLayoutOrder] method call.
         *
         * @param arrayPosition       position in layout order
         * @param itemAdapterPosition adapter position of item for future data filling logic
         * @param itemPositionDiff    difference of current item scroll position and center item position.
         * if this is a center item and it is in real center of layout, then this will be 0.
         * if current layout is not in the center, then this value will never be int.
         * if this item center is bellow layout center line then this value is greater then 0,
         * else less then 0.
         */
        fun setLayoutOrder(arrayPosition: Int, itemAdapterPosition: Int, itemPositionDiff: Float) {
            val item = mLayoutOrder!![arrayPosition]
            item!!.mItemAdapterPosition = itemAdapterPosition
            item.mItemPositionDiff = itemPositionDiff
        }

        /**
         * Checks is this screen Layout has this adapterPosition view in layout
         *
         * @param adapterPosition adapter position of item for future data filling logic
         * @return true is adapterItem is in layout
         */
        fun hasAdapterPosition(adapterPosition: Int): Boolean {
            if (null != mLayoutOrder) {
                for (layoutOrder in mLayoutOrder!!) {
                    if (layoutOrder!!.mItemAdapterPosition == adapterPosition) {
                        return true
                    }
                }
            }
            return false
        }

        private fun recycleItems(vararg layoutOrders: LayoutOrder?) {
            for (layoutOrder in layoutOrders) {
                mReusedItems.add(WeakReference(layoutOrder!!))
            }
        }

        private fun fillLayoutOrder() {
            var i = 0
            val length = mLayoutOrder!!.size
            while (i < length) {
                if (null == mLayoutOrder!![i]) {
                    mLayoutOrder!![i] = createLayoutOrder()
                }
                ++i
            }
        }

        private fun createLayoutOrder(): LayoutOrder {
            val iterator = mReusedItems.iterator()
            while (iterator.hasNext()) {
                val layoutOrderWeakReference = iterator.next()
                val layoutOrder = layoutOrderWeakReference.get()
                iterator.remove()
                if (null != layoutOrder) {
                    return layoutOrder
                }
            }
            return LayoutOrder()
        }

    }

    /**
     * Class that holds item data.
     * This class is filled during [.generateLayoutOrder] and used during [.fillData]
     */
    private class LayoutOrder {
        /**
         * Item adapter position
         */
        var mItemAdapterPosition = 0
        /**
         * Item center difference to layout center. If center of item is bellow layout center, then this value is greater then 0, else it is less.
         */
        var mItemPositionDiff = 0f
    }

    protected class CarouselSavedState : Parcelable {
        val mSuperState: Parcelable?
        var mCenterItemPosition = 0

        constructor(@Nullable superState: Parcelable?) {
            mSuperState = superState
        }

        private constructor(`in`: Parcel) {
            mSuperState = `in`.readParcelable(Parcelable::class.java.classLoader)
            mCenterItemPosition = `in`.readInt()
        }

        constructor(other: CarouselSavedState) {
            mSuperState = other.mSuperState
            mCenterItemPosition = other.mCenterItemPosition
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeParcelable(mSuperState, i)
            parcel.writeInt(mCenterItemPosition)
        }

        //companion object {
            /*val CREATOR: Parcelable.Creator<CarouselSavedState> = object : Parcelable.Creator<CarouselSavedState?> {
                override fun createFromParcel(parcel: Parcel): CarouselSavedState? {
                    return CarouselSavedState(parcel)
                }

                override fun newArray(i: Int): Array<CarouselSavedState?> {
                    return arrayOfNulls(i)
                }
            }
        }

             */

        companion object CREATOR : Parcelable.Creator<CarouselSavedState> {
            override fun createFromParcel(parcel: Parcel): CarouselSavedState {
                return CarouselSavedState(parcel)
            }

            override fun newArray(size: Int): Array<CarouselSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        const val HORIZONTAL = OrientationHelper.HORIZONTAL
        const val VERTICAL = OrientationHelper.VERTICAL
        const val INVALID_POSITION = -1
        const val MAX_VISIBLE_ITEMS = 2
        private const val CIRCLE_LAYOUT = false
        /**
         * Helper method that make scroll in range of [0, count). Generally this method is needed only for cycle layout.
         *
         * @param currentScrollPosition any scroll position range.
         * @param count                 adapter items count
         * @return good scroll position in range of [0, count)
         */
        private fun makeScrollPositionInRange0ToCount(currentScrollPosition: Float, count: Int): Float {
            var absCurrentScrollPosition = currentScrollPosition
            while (0 > absCurrentScrollPosition) {
                absCurrentScrollPosition += count.toFloat()
            }
            while (Math.round(absCurrentScrollPosition) >= count) {
                absCurrentScrollPosition -= count.toFloat()
            }
            return absCurrentScrollPosition
        }
    }
    /**
     * If circleLayout is true then all items will be in cycle. Scroll will be infinite on both sides.
     *
     * @param orientation  should be [.VERTICAL] or [.HORIZONTAL]
     * @param circleLayout true for enabling circleLayout
     */
    /**
     * @param orientation should be [.VERTICAL] or [.HORIZONTAL]
     */
    init {
        require(!(HORIZONTAL != orientation && VERTICAL != orientation)) { "orientation should be HORIZONTAL or VERTICAL" }
        mOrientation = orientation
        mCircleLayout = circleLayout
        mPendingScrollPosition = INVALID_POSITION
    }
}
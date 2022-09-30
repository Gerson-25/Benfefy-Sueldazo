package syntepro.util.picker

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.syntepro.appbeneficiosbolivia.R


/**
 * Created by abelacosta on 12/11/17.
 */

class PickerEditText<T : ListablePicker> : ErrorEditText, OnItemPickListClickListener<T> {
    private var mHint: CharSequence? = null
    private var onItemSelectedListener: OnItemSelectedListener<T>? = null
    private var mPicker: PickListPicker<T>? = null
    var item: T? = null

    constructor(context: Context) : super(context) { mHint = hint }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { mHint = hint }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mHint = hint
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        inputType = InputType.TYPE_NULL
        isFocusable = false
        isClickable = true
    }


    fun with(items: MutableList<T>, title: String, searchBy: Int = PickerDialog.SEARCH_BY_CODE,
             layoutType: Int = PickerDialog.ONE_ROW_LAYOUT): PickerEditText<T> {
        configureOnClickListener(items, title, searchBy, layoutType)
        return this
    }

    private fun configureOnClickListener(lst: MutableList<T>, title: String, searchBy: Int = PickerDialog.SEARCH_BY_CODE, layoutType: Int = PickerDialog.ONE_ROW_LAYOUT) {
        mPicker = PickListPicker.Builder<T>().with(context)
                .data(lst)
                .searchBy(searchBy)
                .layoutType(layoutType)
                .listener(this)
                .build()

        setOnClickListener {
            val activity = unwrap(it.context) as AppCompatActivity
            mPicker?.showDialog(activity.supportFragmentManager, title)
        }
    }

    private fun unwrap(context: Context): Activity? {
        var ctx: Context? = context
        while (ctx !is Activity && ctx is ContextWrapper) {
            ctx = ctx.baseContext
        }
        return ctx as Activity?
    }

    fun getItemByCode(code: String): T? {
        item =  mPicker?.getItemByCode(code)
        if(item != null)
            setText(item?.getTitleValue())
        return item
    }

    fun isNotEmpty(message: String = resources.getString(R.string.required_label)): Boolean {
        if(TextUtils.isEmpty(this.text)) {
            setError(message)
            requestFocus()
            return false
        }else
            this.error = null
        return true
    }

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener<T>) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    override fun onItemClicked(pickList: T, position: Int) {
        setText(pickList.getTitleValue())
        item = pickList
        if (onItemSelectedListener != null) {

            onItemSelectedListener?.onItemSelectedListener(pickList, position)
        }
    }

    override fun onResetClicked() {
        if (onItemSelectedListener != null) {
            onItemSelectedListener?.onResetListener()
        }
    }

    interface OnItemSelectedListener<T> {
        fun onItemSelectedListener(item: T, position: Int)
        fun onResetListener()
    }

    /*override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super<ErrorEditText>.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super<ErrorEditText>.onFocusChanged(focused, direction, previouslyFocusedRect)
    }
*/
    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
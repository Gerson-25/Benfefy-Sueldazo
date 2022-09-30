package syntepro.util.picker

import android.content.Context
import androidx.fragment.app.FragmentManager
import syntepro.util.PickList

import java.util.*

class PickListPicker<T: ListablePicker>//ITEMS = items!!.toTypedArray()
private constructor(builder: Builder<T>) : PickerDialog.PickListPickerDialogInteractionListener<T> {
    private var ITEMS: Array<T>? = null

    private var context: Context? = null
    private var sortBy = SORT_BY_NONE
    private var onPickListPickerListener: OnItemPickListClickListener<T>? = null
    private var canSearch = true
    private var searchBy = PickerDialog.SEARCH_BY_CODE
    private var layoutType: Int = PickerDialog.ONE_ROW_LAYOUT

    private var items: MutableList<T>? = null

    init {
        sortBy = builder.sortBy
        if (builder.onPickListPickerListener != null) {
            onPickListPickerListener = builder.onPickListPickerListener
        }
        context = builder.context
        canSearch = builder.canSearch
        items = builder.list
        searchBy = builder.searchBy
        layoutType = builder.layoutType
        sortItems(items!!)
    }
    // endregion

    // region Listeners
    override fun sortItems(searchResults: List<T>) {
        when (sortBy) {
            SORT_BY_CODE -> {
                Collections.sort(searchResults) { item1, item2 -> item1.getCodeValue().trim { it <= ' ' }.compareTo(item2.getCodeValue().trim { it <= ' ' }, ignoreCase = true) }
                Collections.sort(searchResults) { item1, item2 -> item1.getTitleValue().trim { it <= ' ' }.compareTo(item2.getTitleValue().trim { it <= ' ' }, ignoreCase = true) }
                Collections.sort(searchResults) { item1, item2 -> item1.getDescValue().trim { it <= ' ' }.compareTo(item2.getDescValue().trim { it <= ' ' }, ignoreCase = true) }
            }
            SORT_BY_TITLE -> {
                Collections.sort(searchResults) { item1, item2 -> item1.getCodeValue().trim { it <= ' ' }.compareTo(item2.getCodeValue().trim { it <= ' ' }, ignoreCase = true) }
                Collections.sort(searchResults) { item1, item2 -> item1.getTitleValue().trim { it <= ' ' }.compareTo(item2.getTitleValue().trim { it <= ' ' }, ignoreCase = true) }
            }
            SORT_BY_DESCRIPTION -> Collections.sort(searchResults) { item1, item2 -> item1.getDescValue().trim { it <= ' ' }.compareTo(item2.getDescValue().trim { it <= ' ' }, ignoreCase = true) }
        }
    }

    override fun allItems(): List<T> {
        return items!!
    }

    override fun canSearch(): Boolean {
        return canSearch
    }

    override fun searchBy(): Int {
        return searchBy
    }

    override fun layoutType(): Int {
        return layoutType
    }

    // endregion

    // region Utility Methods
    fun showDialog(supportFragmentManager: FragmentManager, title: String = "") {
        //if (items == null || items!!.isEmpty()) {
        //    throw IllegalArgumentException(context!!.getString(R.string.error_no_items_found))
        //} else {
            val countryPickerDialog = PickerDialog<T>()
            countryPickerDialog.setTitle(title)
            countryPickerDialog.layoutType(layoutType)
            if (onPickListPickerListener != null) {
                countryPickerDialog.setPickListPickerListener(onPickListPickerListener!!)
            }
            countryPickerDialog.setDialogInteractionListener(this)
            countryPickerDialog.show(supportFragmentManager, COUNTRY_TAG)
        //}
    }

    fun setItems(items: List<T>) {
        this.items!!.clear()
        this.items!!.addAll(items)
        sortItems(this.items!!)
    }


    fun getPickListByTitle(pickListTitle: String) /* : T */ {
        var title = pickListTitle
        title = title.toUpperCase()
        val country = PickList()
       /* country.title = title
        val i = Arrays.binarySearch(ITEMS, country, TitleComparator())
        return if (i < 0) {
            null
        } else {
            ITEMS!![i]
        }*/

    }

    fun getItemByCode(pickListCode: String)  : T?  {
        for (n in items?.iterator()!!) {
            if (n.getCodeValue().toUpperCase(Locale.ROOT) == pickListCode.toUpperCase(Locale.ROOT)) {
                return n
                break
            }
        }
        return null
    }
    // endregion

    // region Builder
    class Builder <T : ListablePicker> {
        var context: Context? = null
        var sortBy = SORT_BY_NONE
        var canSearch = true
        var title: String? = null
        var list: MutableList<T>? = null
        var layoutType: Int = PickerDialog.ONE_ROW_LAYOUT
        var searchBy: Int = PickerDialog.SEARCH_BY_CODE
        var onPickListPickerListener: OnItemPickListClickListener<T>? = null

        fun with(context: Context): Builder<T> {
            this.context = context
            return this
        }

        fun data(list: MutableList<T>): Builder<T> {
            this.list = list
            return this
        }
        fun sortBy(sortBy: Int): Builder<T> {
            this.sortBy = sortBy
            return this
        }

        fun searchBy(item: Int): Builder<T> {
            searchBy = item
            return this
        }

        fun layoutType(layoutType: Int): Builder<T> {
            this.layoutType = layoutType
            return this
        }

        fun listener(onCountryPickerListener: OnItemPickListClickListener<T>): Builder<T> {
            this.onPickListPickerListener = onCountryPickerListener
            return this
        }

        fun canSearch(canSearch: Boolean): Builder<T> {
            this.canSearch = canSearch
            return this
        }

        fun build(): PickListPicker<T> {
            return PickListPicker(this)
        }
    }

    internal class TitleComparator : Comparator<ListablePicker> {
        override fun compare(pickList: ListablePicker, nextPickList: ListablePicker): Int {
            return pickList.getTitleValue().compareTo(nextPickList.getTitleValue())
        }
    }

    internal class CodeComparator : Comparator<ListablePicker> {
        override fun compare(pickList: ListablePicker, nextPickList: ListablePicker): Int {
            return pickList.getCodeValue().compareTo(nextPickList.getCodeValue())
        }
    }

    companion object {
        private const val SORT_BY_NONE = 0
        private const val SORT_BY_CODE = 1
        private const val SORT_BY_TITLE = 2
        private const val SORT_BY_DESCRIPTION= 3
        private const val COUNTRY_TAG = "LIST_PICKER"
    }
}

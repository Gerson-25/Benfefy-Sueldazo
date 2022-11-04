package syntepro.util.picker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.syntepro.sueldazo.R
import java.util.*

class PickerDialog<T: ListablePicker> : DialogFragment(), OnItemPickListClickListener<T> {

    private var dialogInteractionListener: PickListPickerDialogInteractionListener<T>? = null
    private var searchEditText: EditText? = null
    private var countriesRecyclerView: RecyclerView? = null
    private var adapter: PickListAdapter<T>? = null
    private var searchResults: MutableList<T>? = null
    private var listener: OnItemPickListClickListener<T>? = null
    private var pickeTitleId: TextView? = null
    private lateinit var closeId: Button
    private lateinit var clearId: Button
    private var title: String = ""
    private var layoutType: Int = ONE_ROW_LAYOUT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.layout_picker, null)
        searchEditText = view.findViewById(R.id.country_code_picker_search)
        countriesRecyclerView = view.findViewById(R.id.countries_recycler_view)
        pickeTitleId = view.findViewById(R.id.pickeTitleId)
        closeId = view.findViewById(R.id.closeId)
        clearId = view.findViewById(R.id.resetId)
        pickeTitleId?.text = title
        setupRecyclerView()
        if (!dialogInteractionListener!!.canSearch()) {
            searchEditText!!.visibility = View.GONE
        }
        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(searchQuery: Editable) {
                search(searchQuery.toString())
            }
        })
        dialog?.window?.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

        closeId.setOnClickListener{ dismiss() }
        clearId.setOnClickListener{ onResetClicked() }
        return view
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun layoutType(layoutType: Int) {
        this.layoutType = layoutType
    }

    override fun onStart() {
        super.onStart()
        val params = dialog?.window?.attributes
        params?.width = LinearLayout.LayoutParams.MATCH_PARENT
        params?.height = LinearLayout.LayoutParams.MATCH_PARENT
        dialog?.window!!.attributes = params
    }

    override fun onResume() {
        super.onResume()
        dialog?.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onItemClicked(pickList: T, position: Int) {
        if (listener != null) {
            listener!!.onItemClicked(pickList, position)
        }
        dismiss()
    }

    override fun onResetClicked() {
        if (listener != null) {
            listener!!.onResetClicked()
        }
        dismiss()
    }

    fun setPickListPickerListener(listener: OnItemPickListClickListener<T>) {
        this.listener = listener
    }

    fun setDialogInteractionListener(
            dialogInteractionListener: PickListPickerDialogInteractionListener<T>) {
        this.dialogInteractionListener = dialogInteractionListener
    }

    private fun search(searchQuery: String) {
        searchResults!!.clear()
        for (country in dialogInteractionListener!!.allItems()) {
            if(dialogInteractionListener!!.searchBy() == SEARCH_BY_CODE) {
                if (country.getCodeValue().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
                    searchResults!!.add(country)
                }
            } else if(dialogInteractionListener!!.searchBy() == SEARCH_BY_NAME) {
                if (country.getTitleValue().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
                    searchResults!!.add(country)
                }
            } else {
                if (country.getDescValue().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
                    searchResults!!.add(country)
                }
            }
        }
        dialogInteractionListener!!.sortItems(searchResults!!)
        adapter!!.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        searchResults = ArrayList()
        searchResults!!.addAll(dialogInteractionListener!!.allItems())
        adapter = PickListAdapter(searchResults!!, this)
        adapter?.setLayoutType(layoutType)
        countriesRecyclerView!!.setHasFixedSize(true)
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        layoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        countriesRecyclerView!!.layoutManager = layoutManager
        countriesRecyclerView!!.adapter = adapter
    }




    interface PickListPickerDialogInteractionListener<T: ListablePicker> {
        fun allItems(): List<T>
        fun sortItems(searchResults: List<T>)
        fun canSearch(): Boolean
        fun searchBy(): Int
        fun layoutType(): Int
    }

    companion object {
        const val SEARCH_BY_CODE = 1
        const val SEARCH_BY_NAME = 2
        const val SEARCH_BY_DESCRIPTION = 3

        const val ONE_ROW_LAYOUT = 1
        const val TWO_ROWS_LAYOUT = 2
    }

}

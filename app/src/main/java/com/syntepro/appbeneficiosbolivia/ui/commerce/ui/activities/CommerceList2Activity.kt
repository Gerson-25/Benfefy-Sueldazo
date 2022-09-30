package com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceByBranchRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceByBranchResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceFilterRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceFilterResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters.CommerceListAdapter
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters.CustomSearchAdapter
import com.syntepro.appbeneficiosbolivia.ui.commerce.viewModel.CommerceViewModel
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Commerce
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_commerce_list2.*
import java.util.ArrayList
import javax.inject.Inject

class CommerceList2Activity : BaseActivity() {

    @Inject
    lateinit var commerceListAdapter: CommerceListAdapter

    private lateinit var commerceViewModel: CommerceViewModel
    private var searchView: SearchView? = null
    private var mAdapter: CustomSearchAdapter? = null
    private var branchId: String? = ""
    private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_commerce_list2)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        commerceViewModel = viewModel(viewModelFactory) {
            observe(commerceByBranch, ::handleCommerce)
            observe(filteredCommerce, ::handleFilteredCommerce)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val branchName = extras.getString("branchName")
            supportActionBar!!.title = branchName
            branchId = extras.getString("branchId")
            val branchFilter = mutableListOf<String>()
            branchFilter.add(branchId ?: "")
            loadCommerce(branchFilter)
            showProgress(true)
        }

        mAdapter = CustomSearchAdapter(this, null)

        initList()

        commerceListAdapter.setActivity(this)

        nestedScroll.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            val nestedScrollView = checkNotNull(v) {
                return@setOnScrollChangeListener
            }
            val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)
            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - nestedScrollView.measuredHeight)) && scrollY > oldScrollY) {
                    // get more items
                    page++
                    showProgress(true)
                    val branchFilter = mutableListOf<String>()
                    branchFilter.add(branchId ?: "")
                    loadCommerce(branchFilter)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("RestrictedApi", "CutPasteId")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_trends_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        // Assumes current activity is the searchable activity
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView!!.setIconifiedByDefault(true)
        searchView!!.maxWidth = Integer.MAX_VALUE
        val searchEditText = searchView!!.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchEditText!!.setTextColor(resources.getColor(R.color.black))
        searchEditText.setHintTextColor(resources.getColor(R.color.black))
        searchEditText.textSize = 14f
        searchEditText.setBackgroundResource(R.drawable.search_text_background)

        searchView!!.suggestionsAdapter = mAdapter

        searchView!!.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = mAdapter!!.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("name"))
                searchView!!.setQuery(txt, true)
                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }
        })

        val c = MatrixCursor(arrayOf(BaseColumns._ID, "reference", "image", "title", "subtitle", "type"))
        addDefaultSuggestions(c)

        val aut = searchView!!.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
        aut.threshold = 0

        searchView!!.setOnCloseListener {
            searchView!!.setQuery("", false)
            searchView!!.clearFocus()
            false
        }

        val searchViewItem = menu.findItem(R.id.action_search)
        searchViewItem.setOnMenuItemClickListener { false }

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView!!.clearFocus()
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                populateAdapter(query)
                return false
            }
        })

        searchView!!.setOnClickListener { searchView!!.clearFocus() }
        return true
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        listId.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.layoutManager = linearLayoutManager
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = commerceListAdapter
        commerceListAdapter.setActivity(this)
    }

    private fun loadCommerce(filter: MutableList<String>?) {
        val request = CommerceByBranchRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            recordsNumber = Constants.LIST_PAGE_SIZE,
            pageNumber = page,
            branchesId = filter
        )
        commerceViewModel.getCommerceByBranch(request)
    }

    private fun handleCommerce(response: BaseResponse<List<CommerceByBranchResponse>>?) {
        showProgress(false)
        response?.data?.let {
            if (page > 1) {
                val temp = commerceListAdapter.collection
                val full = merge(temp, it[0].commerceList)
                commerceListAdapter.collection = full
            } else commerceListAdapter.collection = it[0].commerceList
        }
    }

    fun openDetail(model: Commerce) {
        callIntent<CommerceDetail2Activity> {
            this.putExtra("commerceId", model.idComercio)
        }
    }

    private fun populateAdapter(query: String) {
        if (query.length > 3) {
            val request = CommerceFilterRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                filterText = query
            )
            commerceViewModel.getFilteredCommerce(request)
        }
    }

    private fun handleFilteredCommerce(response: BaseResponse<List<CommerceFilterResponse>>?) {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "reference", "image", "title", "subtitle", "type"))
        val lst = mutableListOf<String>()

        response?.data?.let {
            for ((i, commerce) in it.withIndex()) {
                lst.add(commerce.commerceName)
                cursor.addRow(arrayOf(i, commerce.idCommerce, commerce.urlCommerce, commerce.commerceName, "", Constants.SUGGESTION_COMMERCE))
            }
            addDefaultSuggestions(cursor)
        }
    }

    private fun addDefaultSuggestions(cursor: MatrixCursor) { mAdapter?.changeCursor(cursor) }

    private fun <T> merge(first: List<T>, second: List<T>): List<T> {
        val list: MutableList<T> = ArrayList(first)
        list.addAll(second)
        return list
    }
}

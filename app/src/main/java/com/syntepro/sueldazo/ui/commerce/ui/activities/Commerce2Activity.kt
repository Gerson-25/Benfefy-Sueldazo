package com.syntepro.sueldazo.ui.commerce.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.commerce.ui.adapters.CustomSearchAdapter
import com.syntepro.sueldazo.ui.commerce.model.*
import com.syntepro.sueldazo.ui.commerce.ui.adapters.CommerceByBranchesAdapter
import com.syntepro.sueldazo.ui.commerce.viewModel.CommerceViewModel
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_commerce2.*
import kotlinx.android.synthetic.main.commerce_empty_layout.*
import javax.inject.Inject

class Commerce2Activity: BaseActivity() {

    @Inject
    lateinit var commerceByBranchesAdapter: CommerceByBranchesAdapter

    private lateinit var commerceViewModel: CommerceViewModel
    private var searchView: SearchView? = null
    private var mAdapter: CustomSearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_commerce2)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = getString(R.string.commerce_label)

        commerceViewModel = viewModel(viewModelFactory) {
            observe(commerceByBranch, ::handleCommerce)
            observe(filteredCommerce, ::handleFilteredCommerce)
            failure(failure, ::handleError)
        }

        mAdapter = CustomSearchAdapter(this, null)

        filterId.setOnClickListener {
            callIntent<BranchFilterActivity>(287) {  }
        }

        initList()
        showProgress(true)
        commerceByBranchesAdapter.setActivity(this)
    }

    override fun onStart() {
        super.onStart()
        loadCommerce(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 287 && resultCode == Activity.RESULT_OK) {
            data?.let {
                val filter = it.getStringArrayListExtra("branches")
                loadCommerce(filter?.toMutableList())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?):Boolean {
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

        val aut  = searchView!!.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
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
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.layoutManager = linearLayoutManager
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.adapter = commerceByBranchesAdapter
        commerceByBranchesAdapter.setActivity(this)
    }

    private fun loadCommerce(filter: MutableList<String>?) {
        val request = CommerceByBranchRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                recordsNumber = 3,
                pageNumber = 1,
                branchesId = filter
        )
        commerceViewModel.getCommerceByBranch(request)
    }

    private fun handleCommerce(response: BaseResponse<List<CommerceByBranchResponse>>?) {
        showProgress(false)
        response?.data?.let {
            emptyId.visibility = View.GONE
            val dataModel: MutableList<CommerceByBranchDataModel> = mutableListOf()
            for (b in it) {
                val branchDataModel = CommerceByBranchDataModel()
                branchDataModel.type = 1
                branchDataModel.branchId = b.branchId
                branchDataModel.name = b.name
                branchDataModel.urlImage = b.urlImage
                branchDataModel.commerceCount = b.commerceCount
                dataModel.add(branchDataModel)
//                for (c in b.commerceList) {
//                    val commerceDataModel = CommerceByBranchDataModel()
//                    commerceDataModel.type = 2
//                    commerceDataModel.commerce = c
//                    dataModel.add(commerceDataModel)
//                }
            }
            commerceByBranchesAdapter.collection = dataModel
        } ?: run  {
            commerceByBranchesAdapter.collection = emptyList()
            emptyId.visibility = View.VISIBLE
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

    private fun addDefaultSuggestions(cursor: MatrixCursor) {
        mAdapter?.changeCursor(cursor)
    }

    fun openCommerce(model: CommerceByBranchDataModel) {
        callIntent<CommerceDetail2Activity> {
//            this.putExtra("commerceId", model.commerce.idComercio)
        }
    }

    fun showAll(model: CommerceByBranchDataModel) {
        callIntent<CommerceList2Activity> {
            this.putExtra("branchId", model.branchId)
            this.putExtra("branchName", model.name)
        }
    }

}
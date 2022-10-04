package com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.formatter.IFillFormatter
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.core.di.ApplicationComponent
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.entity.service.*
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.FavoriteData
import com.syntepro.appbeneficiosbolivia.ui.filter.FilterActivity
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CategoryAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.GiftCardsAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters.ArticleAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.CategoryViewModel
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.model.ArticleResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCardRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.adapters.DiscountAdapter
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_shop.*
import kotlinx.android.synthetic.main.coupon_empty_layout.*
import kotlinx.android.synthetic.main.discount_item.view.*
import java.util.ArrayList
import javax.inject.Inject

class ShopActivity: BaseActivity() {

    @Inject
    lateinit var articleAdapter: ArticleAdapter

    @Inject
    lateinit var giftCardsAdapter: GiftCardsAdapter

    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var homeViewModel: HomeViewModel

    private val categoryViewModel by lazy { ViewModelProvider(this).get(CategoryViewModel::class.java) }

    private var searchView: SearchView? = null
    private var type: Int = 1
    private var page: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_shop)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null

        homeViewModel = viewModel(viewModelFactory) {
            observe(giftCards, ::handleGiftCards)
            observe(items, ::handleArticles)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            type = extras.getInt("type", 1)
            if (extras.getBoolean("reloadData", false)) getData(type)
            else {
                // Get Data
                getCategoryData()
                initializeList(type)
            }
        }

        // Spinner
        val documentLit: List<String> = listOf("Producto", "Giftcard")
        val arrayAdapter = object : ArrayAdapter<String>(
                this@ShopActivity,
                android.R.layout.simple_spinner_dropdown_item,
                documentLit) {  }
        typeSpinner.adapter = arrayAdapter

        typeSpinner.setSelection(type - 1)

        typeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.let {
                    val document = it.selectedItem as String
                    type = if (document == "Producto") 1 else 2
                    getData(type)
                    getCategoryData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { type = 1 }
        }

        // Adapter
        categoryAdapter = CategoryAdapter(1) { item -> openCategory(item) }
        articleAdapter.parentActivity(this)
        giftCardsAdapter.parentActivity(this)

        // Category Recycler View
        categoriesListId.setHasFixedSize(true)
        categoriesListId.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoriesListId.itemAnimator = DefaultItemAnimator()
        categoriesListId.adapter = categoryAdapter

        filterZoneId.setOnClickListener {
            val intent = Intent(this@ShopActivity, FilterActivity::class.java)
            startActivityForResult(intent, 1)
        }

        nestedScroll.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            val nestedScrollView = checkNotNull(v) {
                return@setOnScrollChangeListener
            }
            val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)
            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - nestedScrollView.measuredHeight)) && scrollY > oldScrollY) {
                    //get more items
                    page++
                    showProgress(true)
                    getData(type)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        if (!Constants.stateNameFiltered.isNullOrEmpty() && !Constants.cityNameFiltered.isNullOrEmpty()) filterZoneId.text = "$Constants.stateNameFiltered, $Constants.cityNameFiltered"
        else filterZoneId.text = getString(R.string.all_departments)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Constants.stateFiltered = data?.getStringExtra("state")
            Constants.stateNameFiltered = data?.getStringExtra("stateName")
            Constants.cityNameFiltered = data?.getStringExtra("cityName")
            if (!Constants.stateNameFiltered.isNullOrEmpty() && !Constants.cityNameFiltered.isNullOrEmpty()) filterZoneId.text = "${Constants.stateNameFiltered}, ${Constants.cityNameFiltered}"
            else filterZoneId.text = getString(R.string.all_departments)
            getData(type)
        }
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
        searchEditText.setBackgroundResource(R.drawable.search_background)

        searchView!!.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }
        })

        // Autocomplete
        val aut  = searchView!!.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
        // Show Dialog from Search View
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
                Log.e("Text", query)
                return false
            }
        })

        searchView!!.setOnClickListener { searchView!!.clearFocus() }
        return true
    }

    private fun getCategoryData() {
        // Categories
        val request = with(CategoryRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = Functions.getLanguage()
            filterType = type
            this
        }
        categoryViewModel.fetchCategories(request).observe(this, {
            it?.let {
                val all = with(Category()) {
                    idCategory = null
                    name = "Todas"
                    description = "Todas las categorias."
                    urlImage = "https://firebasestorage.googleapis.com/v0/b/beneficios-1b534.appspot.com/o/Extras%2Ffondo_tarjeta_lealtad.png?alt=media&token=fd4848ca-5939-4a3c-9bdc-d193cd593d56"
                    this
                }
                it.add(0, all)
                categoryAdapter.setListData(it)
                categoryAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initializeList(type: Int) {
        couponsListId.removeAllViewsInLayout()
        couponsListId.adapter = null
        if (type == 1) {
            couponsListId.setHasFixedSize(true)
            couponsListId.layoutManager = GridLayoutManager(this, 2)
            couponsListId.itemAnimator = DefaultItemAnimator()
            couponsListId.adapter = articleAdapter
        } else {
            couponsListId.setHasFixedSize(true)
            couponsListId.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            couponsListId.itemAnimator = DefaultItemAnimator()
            couponsListId.adapter = giftCardsAdapter
        }
    }

    private fun getData(type: Int) {
        initializeList(type)

        if (type == 1) {
            val articleRequest = with(ArticleRequest()) {
                country = Constants.userProfile?.actualCountry ?: "BO"
                language = Functions.getLanguage()
                recordsNumber = Constants.LIST_PAGE_SIZE
                pageNumber = page
                sortType = 1
                idCity = Constants.stateFiltered
                idCategory = Constants.categoryFiltered
                idUser = Constants.userProfile?.idUser ?: ""
                this
            }
            homeViewModel.loadItems(articleRequest)
        } else {
            val giftCardRequest = with(GiftCardRequest()) {
                country = Constants.userProfile?.actualCountry ?: "BO"
                language = Functions.getLanguage()
                recordsNumber = Constants.LIST_PAGE_SIZE
                pageNumber = page
                sortType = 1
                idCity = Constants.stateFiltered
                idCategory = Constants.categoryFiltered
                this
            }
            homeViewModel.loadGiftCards(giftCardRequest)
        }
    }

    private fun handleGiftCards(response: BaseResponse<List<GiftCard>>?) {
        showProgress(false)
        response?.data?.let {
            if (it.isNullOrEmpty()) emptyId.visibility = View.VISIBLE else emptyId.visibility = View.GONE
            if (page > 1) {
                emptyId.visibility = View.GONE
                val temp = giftCardsAdapter.collection
                val full = merge(temp, it)
                giftCardsAdapter.collection = full
            } else {
                giftCardsAdapter.collection = it
            }
        }
    }

    private fun handleArticles(response: BaseResponse<List<ArticleResponse>>?) {
        showProgress(false)
        response?.data?.let {
            if (it.isNullOrEmpty()) emptyId.visibility = View.VISIBLE else emptyId.visibility = View.GONE
            if (page > 1) {
                emptyId.visibility = View.GONE
                val temp = articleAdapter.collection
                val full = merge(temp, it)
                articleAdapter.collection = full
            } else {
                articleAdapter.collection = it
            }
        }
    }

    private fun openCategory(item: Any) {
        val model = item as Category
        Constants.categoryFiltered = model.idCategory
        page = 1
        getData(type)
    }

    fun openArticleDetail(articleId: String) {
        val intent = Intent(this@ShopActivity, ShopDetailActivity::class.java)
        intent.putExtra("couponId", articleId)
        intent.putExtra("type", 1)
        startActivity(intent)
    }

    fun openGiftCardDetail(giftCardId: String) {
        val intent = Intent(this@ShopActivity, ShopDetailActivity::class.java)
        intent.putExtra("couponId", giftCardId)
        intent.putExtra("type", 2)
        startActivity(intent)
    }

    fun favorite(item: Any, position: Int) {
        val model = item as ArticleResponse
        if (model.favorite)
            FavoriteData.removeFavorite(model.articleId) { _: String, result: Boolean ->
                if (result) {
                    val viewHolder = couponsListId.findViewHolderForAdapterPosition(position) as ArticleAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(this, R.anim.scale_fav)
                    viewHolder.itemView.favorite.startAnimation(animScale)
                    viewHolder.itemView.favorite.setBackgroundResource(R.drawable.ic_mark_fav)
                }
            }
        else
            FavoriteData.addFavorite(model.articleId) { _: String, result: Boolean ->
                if (result) {
                    val viewHolder = couponsListId.findViewHolderForAdapterPosition(position) as ArticleAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(this, R.anim.scale_fav)
                    viewHolder.itemView.favorite.startAnimation(animScale)
                    viewHolder.itemView.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
                }
            }
    }

    private fun <T> merge(first: List<T>, second: List<T>): List<T> {
        val list: MutableList<T> = ArrayList(first)
        list.addAll(second)
        return list
    }

}

package com.appbenefy.sueldazo.ui.category

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.entity.service.Category
import com.appbenefy.sueldazo.ui.coupon.ui.activities.CouponListActivity
import com.appbenefy.sueldazo.ui.home.adapter.CategoryAdapter
import com.appbenefy.sueldazo.ui.home.viewModel.HomeViewModel
import com.appbenefy.sueldazo.utils.Constants
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity: BaseActivity() {

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var homeViewModel: HomeViewModel
    private var homeProvenance = false
    private var shopType: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_category)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = getString(R.string.categories_label)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            homeProvenance = extras.getBoolean("homeProvenance", false)
            shopType = extras.getInt("selectionType", 1)
        }

        homeViewModel = viewModel(viewModelFactory) {
            observe(categories, ::handleCategories)
            failure(failure, ::handleError)
        }

        // Adapter
        categoryAdapter = CategoryAdapter(2) { item -> openCategory(item) }
        initList()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        listId.layoutManager = GridLayoutManager(this, 2)
        listId.itemAnimator = DefaultItemAnimator()
        listId.adapter = categoryAdapter
    }

    private fun handleCategories(response: BaseResponse<List<Category>>?) {
        response?.data?.let {
            categoryAdapter.setListData(it.toMutableList())
            categoryAdapter.notifyDataSetChanged()
        }
    }

    private fun openCategory(item: Any) {
        val model = item as Category
        if (homeProvenance) {
            Constants.categoryFiltered = model.idCategory
        } else {
            callIntent<CouponListActivity> {
                this.putExtra("categoryId", model.idCategory)
                this.putExtra("categoryName", model.name)
            }
        }
    }

}
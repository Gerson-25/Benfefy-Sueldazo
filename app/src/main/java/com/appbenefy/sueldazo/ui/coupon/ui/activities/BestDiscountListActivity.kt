package com.appbenefy.sueldazo.ui.coupon.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountResponse
import com.appbenefy.sueldazo.ui.coupon.ui.adapters.BestDiscountListAdapter
import com.appbenefy.sueldazo.ui.coupon.ui.adapters.BestDiscountMosaicAdapter
import com.appbenefy.sueldazo.ui.coupon.viewModel.CouponViewModel
import kotlinx.android.synthetic.main.activity_best_discount.*
import javax.inject.Inject

class BestDiscountListActivity : BaseActivity() {

    @Inject
    lateinit var bestDiscountListAdapter: BestDiscountListAdapter

    @Inject
    lateinit var bestDiscountMosaicAdapter: BestDiscountMosaicAdapter

    private lateinit var couponViewModel: CouponViewModel
    private var mosaicList: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_best_discount)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = getString(R.string.best)

        couponViewModel = viewModel(viewModelFactory) {
            observe(bestDiscounts, ::handleBestDiscounts)
            failure(failure, ::handleError)
        }

        initList()

        alterListId.setOnClickListener {
            if (mosaicList) {
                mosaicList = false
                alterListId.setImageDrawable(getDrawable(R.drawable.ic_mosaic))
                initList()
            } else {
                mosaicList = true
                alterListId.setImageDrawable(getDrawable(R.drawable.ic_list))
                initMosaic()
            }
        }

        loadBestDiscounts()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initList() {
        listId.setHasFixedSize(true)
        listId.isNestedScrollingEnabled = false
        listId.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listId.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        listId.addItemDecoration(
                DividerItemDecoration(
                        this,
                        LinearLayoutManager.VERTICAL
                )
        )
        listId.adapter = null
        listId.adapter = bestDiscountListAdapter
        bestDiscountListAdapter.setActivity(this)
    }

    private fun initMosaic() {
        listId.setHasFixedSize(true)
        listId.isNestedScrollingEnabled = false
        listId.layoutManager = GridLayoutManager(this, 2)
        listId.itemAnimator = null
        listId.adapter = null
        listId.adapter = bestDiscountMosaicAdapter
        bestDiscountMosaicAdapter.setActivity(this)
    }

    private fun loadBestDiscounts() {
//        showProgress(true)
//        val request = BestDiscountRequest(
//            country = Constants.userProfile?.actualCountry ?: "BO",
//            language = Functions.getLanguage(),
//        )
//        couponViewModel.getBestDiscounts(request)
    }

    private fun handleBestDiscounts(response: BaseResponse<List<BestDiscountResponse>>?) {
        if(response?.data.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
        showProgress(false)
        bestDiscountListAdapter.collection = response?.data.orEmpty()
        bestDiscountMosaicAdapter.collection = response?.data.orEmpty()
    }

    fun openDetail(id: String) {
        callIntent<CouponDetail2Activity> {
            this.putExtra("couponId", id)
        }
    }

//    fun favorite(item: Any, position: Int) {
//        val model = item as BestDiscountResponse
//        if (model.favorite)
//            FavoriteData.removeFavorite(model.idCoupon) { message: String, result: Boolean ->
//                if (result) {
//                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as BestDiscountListAdapter.ViewHolder
//                    val animScale = AnimationUtils.loadAnimation(this, R.anim.scale_fav)
//                    viewHolder.view.favorite.startAnimation(animScale)
//                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_mark_fav)
//                } else Log.e("Favorite", message)
//            }
//        else
//            FavoriteData.addFavorite(model.idCoupon) { message: String, result: Boolean ->
//                if (result) {
//                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as BestDiscountListAdapter.ViewHolder
//                    val animScale = AnimationUtils.loadAnimation(this, R.anim.scale_fav)
//                    viewHolder.view.favorite.startAnimation(animScale)
//                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
//                } else Log.e("Favorite", message)
//            }
//    }

}
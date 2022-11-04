package com.syntepro.sueldazo.ui.coupon.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.coupon.ui.FavoriteData
import com.syntepro.sueldazo.ui.coupon.model.CouponListRequest
import com.syntepro.sueldazo.ui.coupon.model.CouponListResponse
import com.syntepro.sueldazo.ui.coupon.ui.adapters.CouponListAdapter
import com.syntepro.sueldazo.ui.coupon.ui.adapters.CouponMosaicAdapter
import com.syntepro.sueldazo.ui.coupon.viewModel.CouponViewModel
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_coupon_list.*
import kotlinx.android.synthetic.main.coupon_item.view.*
import java.util.*
import javax.inject.Inject

class CouponListActivity : BaseActivity() {

    @Inject
    lateinit var couponListAdapter: CouponListAdapter

    @Inject
    lateinit var couponMosaicAdapter: CouponMosaicAdapter

    private lateinit var couponViewModel: CouponViewModel
    private var page: Int = 1
    private var categoryId: String? = null
    private var commerceId: String? = null
    private var mosaicList: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_coupon_list)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.mainToolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        couponViewModel = viewModel(viewModelFactory) {
            observe(couponList, ::handleCouponList)
            failure(failure, ::handleError)
        }

        // Extras
        val extras = intent.extras
        if (extras != null) {
            categoryId = extras.getString("categoryId", null)
            commerceId = extras.getString("commerceId", null)
            val categoryName = extras.getString("categoryName", "")
            val commerceName = extras.getString("commerceName")
            supportActionBar!!.title = if (categoryName.isNullOrEmpty()) commerceName ?: "" else categoryName
            loadCouponList()
            showProgress(true)
        }

        initList()

        couponListAdapter.setActivity(this)
        couponMosaicAdapter.setActivity(this)

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
                    loadCouponList()
                }
            }
        }

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
        listId.adapter = couponListAdapter
        couponListAdapter.setActivity(this)
    }

    private fun initMosaic() {
        listId.setHasFixedSize(true)
        listId.isNestedScrollingEnabled = false
        listId.layoutManager = GridLayoutManager(this, 2)
        listId.itemAnimator = null
        listId.adapter = null
        listId.adapter = couponMosaicAdapter
        couponMosaicAdapter.setActivity(this)
    }

    private fun loadCouponList() {
        val request = CouponListRequest(
                country = Constants.userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                recordsNumber = Constants.LIST_PAGE_SIZE,
                pageNumber = page,
                idUser = Constants.userProfile?.idUser ?: "",
                idCategory = categoryId,
                idCommerce = commerceId
        )
        couponViewModel.getCouponList(request)
    }

    private fun handleCouponList(response: BaseResponse<List<CouponListResponse>>?) {
        showProgress(false)
        response?.data?.let {
            if(it.isNullOrEmpty()) showEmptyLayout(true) else showEmptyLayout(false)
            if (page > 1) {
                showEmptyLayout(false)
                val temp = couponListAdapter.collection
                val full = merge(temp, it)
                couponListAdapter.collection = full
                couponMosaicAdapter.collection = full
            } else {
                couponListAdapter.collection = it
                couponMosaicAdapter.collection = it
            }
        } ?: run {
            showEmptyLayout(true)
        }
    }

    private fun <T> merge(first: List<T>, second: List<T>): List<T> {
        val list: MutableList<T> = ArrayList(first)
        list.addAll(second)
        return list
    }

    fun openDetail(id: String, type: Int) {
        val loyaltyType = if (type == 5) 1 else if (type == 6) 2 else 0
        callIntent<CouponDetail2Activity> {
            this.putExtra("couponId", id)
            if (loyaltyType != 0) this.putExtra("loyaltyType", loyaltyType)
        }
    }

    fun favorite(item: Any, position: Int) {
        val model = item as CouponListResponse
        if (model.favorite)
            FavoriteData.removeFavorite(model.idCoupon) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as CouponListAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(this, R.anim.scale_fav)
                    viewHolder.view.favorite.startAnimation(animScale)
                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_mark_fav)
                } else Log.e("Favorite", message)
            }
        else
            FavoriteData.addFavorite(model.idCoupon) { message: String, result: Boolean ->
                if (result) {
                    val viewHolder = listId.findViewHolderForAdapterPosition(position) as CouponListAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(this, R.anim.scale_fav)
                    viewHolder.view.favorite.startAnimation(animScale)
                    viewHolder.view.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
                } else Log.e("Favorite", message)
            }
    }

}
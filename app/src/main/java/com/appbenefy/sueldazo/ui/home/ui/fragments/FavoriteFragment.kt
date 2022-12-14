package com.appbenefy.sueldazo.ui.home.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.base.BaseFragment
import com.appbenefy.sueldazo.core.base.viewModel
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.entity.service.Category
import com.appbenefy.sueldazo.ui.category.CategoryActivity
import com.appbenefy.sueldazo.ui.commerce.ui.activities.Commerce2Activity
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountRequest
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountResponse
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponRequest
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponResponse
import com.appbenefy.sueldazo.ui.coupon.ui.FavoriteData
import com.appbenefy.sueldazo.ui.coupon.ui.activities.CouponDetail2Activity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.CouponListActivity
import com.appbenefy.sueldazo.ui.home.adapter.CategoryAdapter
import com.appbenefy.sueldazo.ui.home.model.CategoryRequest
import com.appbenefy.sueldazo.ui.home.ui.adapters.BestDiscountAdapter
import com.appbenefy.sueldazo.ui.home.ui.adapters.FeaturedAdapter
import com.appbenefy.sueldazo.ui.home.ui.dialog.NotRegisterDialog
import com.appbenefy.sueldazo.ui.home.viewModel.HomeViewModel
import com.appbenefy.sueldazo.ui.location.LocationService
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.empty_featured_coupons.*
import kotlinx.android.synthetic.main.empty_home_discount_layout.*
import kotlinx.android.synthetic.main.featured_item.view.*
import kotlinx.android.synthetic.main.fragment_favorite.*
import javax.inject.Inject

class FavoriteFragment : BaseFragment() {

    @Inject
    lateinit var featuredAdapter: FeaturedAdapter

    @Inject
    lateinit var bestDiscountAdapter: BestDiscountAdapter

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var homeViewModel: HomeViewModel
    private var lastLongitude: Double? = null
    private var lastLatitude: Double? = null

    override fun layoutId() = R.layout.fragment_favorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        homeViewModel = viewModel(viewModelFactory) {
            observe(categories, ::handleCategories)
            observe(featuredCoupons, ::handleFeaturedCoupons)
            observe(bestDiscounts, ::handleBestDiscounts)
            failure(failure, ::handleFailure)
        }

        categoryAdapter = CategoryAdapter(1) { item -> openCategory(item) }
        featuredAdapter.parentFragment(this)
        bestDiscountAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getCategories()
        getBestDiscounts("51C5CDE9-C6C0-46D9-9FDE-4E6DA357EC3F", null, null)
        getFeaturedCoupons("51C5CDE9-C6C0-46D9-9FDE-4E6DA357EC3F")
        moreCategories.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            startActivity(intent)
        }

        sortBy.setOnClickListener {
            if (runtimePermissions()) {
                LocationService.getFusedLocation(requireActivity()) { long, lat ->
                    lastLongitude = long
                    lastLatitude = lat
                    getBestDiscounts("51C5CDE9-C6C0-46D9-9FDE-4E6DA357EC3F", lastLatitude, lastLongitude)
                }
            } else requestLocationPermissions()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                LocationService.getFusedLocation(requireContext()) { long, lat ->
                    lastLongitude = long
                    lastLatitude = lat
                    getBestDiscounts("51C5CDE9-C6C0-46D9-9FDE-4E6DA357EC3F", lastLatitude, lastLongitude)
                }
            }
        }
    }

    private fun runtimePermissions(): Boolean {
        return Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initList() {
        // Category Recycler View
        categoriesListId.setHasFixedSize(true)
        categoriesListId.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoriesListId.itemAnimator = DefaultItemAnimator()
        categoriesListId.adapter = categoryAdapter

        // Featured Recycler View
        featuredCouponListId.setHasFixedSize(true)
        featuredCouponListId.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        featuredCouponListId.itemAnimator = DefaultItemAnimator()
        featuredCouponListId.adapter = featuredAdapter

        // Best Discounts Recycler View
        CouponListId.setHasFixedSize(true)
        CouponListId.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        CouponListId.itemAnimator = DefaultItemAnimator()
        CouponListId.adapter = bestDiscountAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getCategories() {
        val request = CategoryRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = 0
        )
        homeViewModel.loadCategories(request)

//        val category1 = Category()
//        category1.description = ""
//        category1.idCategory = "1"
//        category1.urlImage = ""
//        category1.name = "Mascotas"
//
//        val category2 = Category()
//        category2.description = ""
//        category2.idCategory = "2"
//        category2.urlImage = ""
//        category2.name = "Bebidas"
//
//        val category3 = Category()
//        category3.description = ""
//        category3.idCategory = "3"
//        category3.urlImage = ""
//        category3.name = "Snacks"
//
//        val category5 = Category()
//        category5.description = ""
//        category5.idCategory = "4"
//        category5.urlImage = ""
//        category5.name = "Alimentos"
//
//        val list = listOf(
//            category1, category2, category3, category5
//        )
//        categoryAdapter.setListData(list.toMutableList())
//        categoryAdapter.notifyDataSetChanged()
    }

    private fun getBestDiscounts(category: String, latitude: Double?, longitude: Double?) {
        showLoading(true)
        val request = BestDiscountRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            recordsNumber = 20,
            pageNumber = 1,
            idCategoryType = category,
            latitude = latitude,
            longitude = longitude
        )
        homeViewModel.loadBestDiscounts(request)
    }

    private fun getFeaturedCoupons(category: String) {
        showLoading(true)
        val request = FeaturedCouponRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            recordsNumber = 20,
            pageNumber = 1,
            idCategoryType = category
        )
        homeViewModel.loadFeaturedCoupons(request)
    }

    private fun handleCategories(categories: BaseResponse<List<Category>>?) {
        categories?.data?.let {
            categoryAdapter.setListData(it.toMutableList())
            categoryAdapter.notifyDataSetChanged()
        }
    }

    private fun handleBestDiscounts(response: BaseResponse<List<BestDiscountResponse>>?) {
        showLoading(false)
        bestDiscountAdapter.collection = response?.data.orEmpty()
        if (!response?.data.isNullOrEmpty()) emptyDiscountId.visibility = View.GONE else emptyDiscountId.visibility = View.VISIBLE
    }

    private fun handleFeaturedCoupons(response: BaseResponse<List<FeaturedCouponResponse>>?) {
        showLoading(false)
        featuredAdapter.collection = response?.data.orEmpty()
        if (!response?.data.isNullOrEmpty()) emptyFeaturedId.visibility = View.GONE else emptyFeaturedId.visibility = View.VISIBLE
    }

    private fun requestLocationPermissions() {
        val locationPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= 23)
            requireActivity().requestPermissions(locationPermission, 100)
    }

    fun openBestDiscountDetail(idCommerce: String) {
//        val intent = Intent(requireContext(), CouponDetail2Activity::class.java)
        val intent = Intent(requireContext(), CouponDetail2Activity::class.java)
        intent.putExtra("couponId", idCommerce)
        intent.putExtra("loyaltyType", true)
        startActivity(intent)
    }

    fun showBlocked() = Constants.userProfile?.isAnonymousUser

    private fun showLoading(show:  Boolean){
        loadingBenefits.visibility = if (show) View.VISIBLE else View.GONE
        loadingFeatures.visibility = if (show) View.VISIBLE else View.GONE

        featuredCouponListId.visibility = if (show) View.GONE else View.VISIBLE
        CouponListId.visibility = if (show) View.GONE else View.VISIBLE
}

    fun openDetail(type: Int, id: String, loyaltyType: Int) {
        if (type == 1) {
            val intent = Intent(requireContext(), CouponDetail2Activity::class.java)
            intent.putExtra("couponId", id)
            if (loyaltyType != 0) intent.putExtra("loyaltyType", loyaltyType)
            startActivity(intent)
        }
    }

    private fun openCategory(item: Category) {
        getBestDiscounts(item.idCategory?: "51C5CDE9-C6C0-46D9-9FDE-4E6DA357EC3F", lastLatitude, lastLongitude)
        getFeaturedCoupons(item.idCategory?: "51C5CDE9-C6C0-46D9-9FDE-4E6DA357EC3F")
    }

    fun notRegisterUser() {
        val intent = Intent(requireContext(), NotRegisterDialog::class.java)
        startActivity(intent)
    }

    fun favorite(item: Any, position: Int) {
        val model = item as FeaturedCouponResponse
        if (model.favorite)
            FavoriteData.removeFavorite(model.idCoupon) { _: String, result: Boolean ->
                if (result) {
                    val viewHolder = featuredCouponListId.findViewHolderForAdapterPosition(position) as FeaturedAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.itemView.favorite.startAnimation(animScale)
                    viewHolder.itemView.favorite.setBackgroundResource(R.drawable.ic_mark_fav)
                }
            }
        else
            FavoriteData.addFavorite(model.idCoupon) { _: String, result: Boolean ->
                if (result) {
                    val viewHolder = featuredCouponListId.findViewHolderForAdapterPosition(position) as FeaturedAdapter.ViewHolder
                    val animScale = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_fav)
                    viewHolder.itemView.favorite.startAnimation(animScale)
                    viewHolder.itemView.favorite.setBackgroundResource(R.drawable.ic_unmark_fav)
                }
            }
    }
}

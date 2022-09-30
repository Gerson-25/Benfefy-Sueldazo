package com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.core.base.viewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.ui.category.CategoryActivity
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.Commerce2Activity
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.CommerceDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.BestDiscountRequest
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.BestDiscountResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.FeaturedCouponResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.FavoriteData
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponListActivity
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CategoryAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.model.CategoryRequest
import com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters.BestDiscountAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters.FeaturedAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.location.LocationService
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities.NotificationsActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.ShopDetailActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
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
    private var sortTypeCode = BestDiscountRequest.SORT_TYPE_DISCOUNT
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

        Functions.readUserInfo(userImageId, welcomeId, total_notificationsId)

        cardImage.setOnClickListener { (activity as HomeActivity).openDrawer() }

        scanId.setOnClickListener {
            val integrator = IntentIntegrator(requireActivity())
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scanner")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setOrientationLocked(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()
        }

        notificationsId.setOnClickListener {
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent)
        }

        moreCategories.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            startActivity(intent)
        }

        moreCommerces.setOnClickListener {
            val intent = Intent(requireContext(), Commerce2Activity::class.java)
            startActivity(intent)
        }

        sortBy.setOnClickListener {
            when (sortTypeCode) {
                BestDiscountRequest.SORT_TYPE_DISCOUNT -> {
                    if (runtimePermissions()) {
                        LocationService.getFusedLocation(requireActivity()) { long, lat ->
                            lastLongitude = long
                            lastLatitude = lat
                            switchSort()
                            getBestDiscounts()
                        }
                    } else requestLocationPermissions()
                }
                else -> {
                    lastLongitude = null
                    lastLatitude = null
                    switchSort()
                    getBestDiscounts()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                LocationService.getFusedLocation(requireContext()) { long, lat ->
                    lastLongitude = long
                    lastLatitude = lat
                    getBestDiscounts()
                    switchSort()
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

    private fun getCategories() {
        val request = CategoryRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = 0
        )
        homeViewModel.loadCategories(request)
    }

    private fun getBestDiscounts() {
        val request = BestDiscountRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            idUser = Constants.userProfile?.idUser ?: "",
            sortType = sortTypeCode,
            longitude = lastLongitude,
            latitude = lastLatitude
        )
        homeViewModel.loadBestDiscounts(request)
    }

    private fun handleCategories(categories: BaseResponse<List<Category>>?) {
        categories?.data?.let {
            categoryAdapter.setListData(it.toMutableList())
            categoryAdapter.notifyDataSetChanged()
        }
    }

    private fun handleBestDiscounts(response: BaseResponse<List<BestDiscountResponse>>?) {
        bestDiscountAdapter.collection = response?.data.orEmpty()
        if (!response?.data.isNullOrEmpty()) emptyDiscountId.visibility = View.GONE else emptyDiscountId.visibility = View.VISIBLE
    }

    private fun handleFeaturedCoupons(response: BaseResponse<List<FeaturedCouponResponse>>?) {
        featuredAdapter.collection = response?.data.orEmpty()
        if (!response?.data.isNullOrEmpty()) emptyFeaturedId.visibility = View.GONE else emptyFeaturedId.visibility = View.VISIBLE
    }

    private fun requestLocationPermissions() {
        val locationPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= 23)
            requireActivity().requestPermissions(locationPermission, 100)
    }

    private fun switchSort() {
        if (sortTypeCode == BestDiscountRequest.SORT_TYPE_DISCOUNT) {
            sortTypeCode = BestDiscountRequest.SORT_TYPE_LOCATION
            sortBy.setImageDrawable(requireContext().getDrawable(R.drawable.ic_percentage))
        } else {
            sortTypeCode = BestDiscountRequest.SORT_TYPE_DISCOUNT
            sortBy.setImageDrawable(requireContext().getDrawable(R.drawable.ic_location))
        }
    }

    fun openBestDiscountDetail(idCommerce: String) {
        val intent = Intent(requireContext(), CommerceDetail2Activity::class.java)
        intent.putExtra("commerceId", idCommerce)
        intent.putExtra("navigate", true)
        startActivity(intent)
    }

    fun openDetail(type: Int, id: String, loyaltyType: Int) {
        if (type == 1) {
            val intent = Intent(requireContext(), CouponDetail2Activity::class.java)
            intent.putExtra("couponId", id)
            if (loyaltyType != 0) intent.putExtra("loyaltyType", loyaltyType)
            startActivity(intent)
        } else {
            val intent = Intent(requireContext(), ShopDetailActivity::class.java)
            intent.putExtra("couponId", id)
            intent.putExtra("type", 1)
            startActivity(intent)
        }
    }

    private fun openCategory(item: Any) {
        val model = item as Category
        val intent = Intent(requireContext(), CouponListActivity::class.java)
        intent.putExtra("categoryId", model.idCategory)
        intent.putExtra("categoryName", model.name)
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

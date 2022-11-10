package com.syntepro.sueldazo.ui.home.ui.fragments

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
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.core.base.BaseFragment
import com.syntepro.sueldazo.core.base.viewModel
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.entity.service.Category
import com.syntepro.sueldazo.ui.category.CategoryActivity
import com.syntepro.sueldazo.ui.commerce.ui.activities.Commerce2Activity
import com.syntepro.sueldazo.ui.commerce.ui.activities.CommerceDetail2Activity
import com.syntepro.sueldazo.ui.coupon.model.BestDiscountRequest
import com.syntepro.sueldazo.ui.coupon.model.BestDiscountResponse
import com.syntepro.sueldazo.ui.coupon.model.FeaturedCouponRequest
import com.syntepro.sueldazo.ui.coupon.model.FeaturedCouponResponse
import com.syntepro.sueldazo.ui.coupon.ui.FavoriteData
import com.syntepro.sueldazo.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.sueldazo.ui.coupon.ui.activities.CouponListActivity
import com.syntepro.sueldazo.ui.coupon.ui.activities.RatingActivity
import com.syntepro.sueldazo.ui.home.adapter.CategoryAdapter
import com.syntepro.sueldazo.ui.home.model.CategoryRequest
import com.syntepro.sueldazo.ui.home.ui.adapters.BestDiscountAdapter
import com.syntepro.sueldazo.ui.home.ui.adapters.FeaturedAdapter
import com.syntepro.sueldazo.ui.home.viewModel.HomeViewModel
import com.syntepro.sueldazo.ui.location.LocationService
import com.syntepro.sueldazo.ui.shop.ui.activities.ShopDetailActivity
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
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
        getBestDiscounts()
        getFeaturedCoupons()
        moreCategories.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            startActivity(intent)
        }

        moreCommerces.setOnClickListener {
            val intent = Intent(requireContext(), Commerce2Activity::class.java)
            startActivity(intent)
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
//        val request = CategoryRequest(
//            country = Constants.userProfile?.actualCountry ?: "BO",
//            language = Functions.getLanguage(),
//            filterType = 0
//        )
//        homeViewModel.loadCategories(request)

        val category1 = Category()
        category1.description = ""
        category1.idCategory = "1"
        category1.urlImage = ""
        category1.name = "Mascotas"

        val category2 = Category()
        category2.description = ""
        category2.idCategory = "2"
        category2.urlImage = ""
        category2.name = "Bebidas"

        val category3 = Category()
        category3.description = ""
        category3.idCategory = "3"
        category3.urlImage = ""
        category3.name = "Snacks"

        val category5 = Category()
        category5.description = ""
        category5.idCategory = "4"
        category5.urlImage = ""
        category5.name = "Alimentos"

        val list = listOf(
            category1, category2, category3, category5
        )
        categoryAdapter.setListData(list.toMutableList())
        categoryAdapter.notifyDataSetChanged()
    }

    private fun getBestDiscounts() {
        val request = BestDiscountRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            recordsNumber = 20,
            pageNumber = 1,
            idCategoryType = "51C5CDE9-C6C0-46D9-9FDE-4E6DA357EC3F"
        )
        homeViewModel.loadBestDiscounts(request)
    }

    private fun getFeaturedCoupons() {
//        val request = FeaturedCouponRequest(
//            country = Constants.userProfile?.actualCountry ?: "BO",
//            language = 1,
//            idUser = Constants.userProfile?.idUser ?: ""
//        )
//        homeViewModel.loadFeaturedCoupons(request)

        val bestDiscount1 = FeaturedCouponResponse(
            idCoupon = "1",
            title="Best Discount 1",
            subtitle = "Best Discount 1",
            discountPrice = 0.00,
            realPrice = 0.00,
            couponImage = "",
            commerceImage = "https://i1.wp.com/www.wipremiertrivia.com/wp-content/uploads/2019/10/Pizza.jpg?w=1972",
            commerceName = "commerce 1",
            idCommerce = "1",
            couponType = 1,
            favorite = false,
            vip = false
        )

        val bestDiscount2 = FeaturedCouponResponse(
            idCoupon = "1",
            title="Best Discount 2",
            subtitle = "Best Discount 2",
            discountPrice = 0.00,
            realPrice = 0.00,
            couponImage = "",
            commerceImage = "https://i1.wp.com/www.wipremiertrivia.com/wp-content/uploads/2019/10/Pizza.jpg?w=1972",
            commerceName = "commerce 2",
            idCommerce = "2",
            couponType = 2,
            favorite = false,
            vip = false
        )

        val bestDiscount3 = FeaturedCouponResponse(
            idCoupon = "3",
            title="Best Discount 3",
            subtitle = "Best Discount 3",
            discountPrice = 0.00,
            realPrice = 0.00,
            couponImage = "",
            commerceImage = "https://i1.wp.com/www.wipremiertrivia.com/wp-content/uploads/2019/10/Pizza.jpg?w=1972",
            commerceName = "commerce 3",
            idCommerce = "3",
            couponType = 3,
            favorite = false,
            vip = false
        )

        val bestDiscount4 = FeaturedCouponResponse(
            idCoupon = "4",
            title="Best Discount 4",
            subtitle = "Best Discount 4",
            discountPrice = 0.00,
            realPrice = 0.00,
            couponImage = "",
            commerceImage = "https://i1.wp.com/www.wipremiertrivia.com/wp-content/uploads/2019/10/Pizza.jpg?w=1972",
            commerceName = "commerce 4",
            idCommerce = "4",
            couponType = 4,
            favorite = false,
            vip = false
        )

        val bestDiscount5 = FeaturedCouponResponse(
            idCoupon = "5",
            title="Best Discount 5",
            subtitle = "Best Discount 5",
            discountPrice = 0.00,
            realPrice = 0.00,
            couponImage = "",
            commerceImage = "https://i1.wp.com/www.wipremiertrivia.com/wp-content/uploads/2019/10/Pizza.jpg?w=1972",
            commerceName = "commerce 5",
            idCommerce = "5",
            couponType = 4,
            favorite = false,
            vip = false
        )

        val bestDiscount6 = FeaturedCouponResponse(
            idCoupon = "6",
            title="Best Discount 6",
            subtitle = "Best Discount 6",
            discountPrice = 0.00,
            realPrice = 0.00,
            couponImage = "",
            commerceImage = "https://i1.wp.com/www.wipremiertrivia.com/wp-content/uploads/2019/10/Pizza.jpg?w=1972",
            commerceName = "commerce 6",
            idCommerce = "6",
            couponType = 4,
            favorite = false,
            vip = false
        )

        val list = listOf(
            bestDiscount1, bestDiscount2, bestDiscount3, bestDiscount4, bestDiscount5, bestDiscount6
        )

        featuredAdapter.collection = list
        featuredAdapter.notifyDataSetChanged()
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

    fun openBestDiscountDetail(idCommerce: String) {
//        val intent = Intent(requireContext(), CouponDetail2Activity::class.java)
        val intent = Intent(requireContext(), RatingActivity::class.java)
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

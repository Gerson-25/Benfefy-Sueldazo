package com.syntepro.sueldazo.ui.home.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.core.base.BaseFragment
import com.syntepro.sueldazo.core.base.viewModel
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.entity.service.Category
import com.syntepro.sueldazo.ui.category.CategoryActivity
import com.syntepro.sueldazo.ui.coupon.ui.FavoriteData
import com.syntepro.sueldazo.ui.home.HomeActivity
import com.syntepro.sueldazo.ui.home.adapter.CategoryAdapter
import com.syntepro.sueldazo.ui.home.adapter.GiftCardsAdapter
import com.syntepro.sueldazo.ui.home.model.*
import com.syntepro.sueldazo.ui.home.ui.adapters.ArticleAdapter
import com.syntepro.sueldazo.ui.home.ui.adapters.BannerAdapter
import com.syntepro.sueldazo.ui.home.ui.adapters.FeaturedGiftCardAdapter
import com.syntepro.sueldazo.ui.home.viewModel.HomeViewModel
import com.syntepro.sueldazo.ui.notifications.model.NotificationCountResponse
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse
import com.syntepro.sueldazo.ui.shop.model.GiftCard
import com.syntepro.sueldazo.ui.shop.ui.activities.ShopActivity
import com.syntepro.sueldazo.ui.shop.ui.activities.ShopDetailActivity
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.coupon_empty_layout.*
import kotlinx.android.synthetic.main.discount_item.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    @Inject
    lateinit var articleAdapter: ArticleAdapter

    @Inject
    lateinit var bannerAdapter: BannerAdapter

    @Inject
    lateinit var featuredGiftCardAdapter: FeaturedGiftCardAdapter

    @Inject
    lateinit var giftCardsAdapter: GiftCardsAdapter

    private lateinit var homeViewModel: HomeViewModel

    override fun layoutId() = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        homeViewModel = viewModel(viewModelFactory) {
            observe(banners, ::handleBanners)
            observe(parameters, ::parametersResponse)
            observe(counter, ::handleCounter)
            observe(states, ::handleStates)
            failure(failure, ::handleFailure)
        }

        bannerAdapter.parentFragment(this)

        // Adapters
        articleAdapter.parentFragment(this)
        featuredGiftCardAdapter.parentFragment(this)
        giftCardsAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerview()
        getBanners()


    }

    private fun configureRecyclerview() {
        bannerItemList.removeAllViewsInLayout()
        bannerItemList.adapter = null
        bannerItemList.setHasFixedSize(true)
        bannerItemList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        bannerItemList.itemAnimator = DefaultItemAnimator()
        bannerItemList.isLayoutFrozen = true
        bannerItemList.adapter = bannerAdapter
    }

    private fun getBanners() {
        val request = BannerRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = Functions.getLanguage(),
            filterType = 0
        )
        homeViewModel.loadBanners(request)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleBanners(categories: BaseResponse<List<BannerResponse>>?) {
        categories?.data?.let {
            bannerAdapter.collection = it
            bannerAdapter.notifyDataSetChanged()
        }
    }

    private fun parametersResponse(response: BaseResponse<ParameterResponse>?) {
        response?.data?.let {
            Constants.sudamericanaParameters = it.SUDAMERICANA_PARAMETROS
            Constants.transactionParameters = it.transactionsType
        }
    }

    private fun handleCounter(response: BaseResponse<NotificationCountResponse>?) {
        Constants.NOTIFICATION_COUNTER = response?.data?.count ?: 0
    }

    private fun handleStates(response: BaseResponse<List<StatesResponse>>?) {
        Constants.countryStates = response?.data?.toMutableList()
    }

    fun openArticleDetail(articleId: String) {
        val intent = Intent(requireContext(), ShopDetailActivity::class.java)
        intent.putExtra("couponId", articleId)
        intent.putExtra("type", 1)
        startActivity(intent)
    }

    fun openBanner(item: Any) {
        val model = item as BannerResponse
        Functions.openURL(model.urlSite, requireContext())
    }

    fun openGiftCardDetail(giftCardId: String) {
        val intent = Intent(requireContext(), ShopDetailActivity::class.java)
        intent.putExtra("couponId", giftCardId)
        intent.putExtra("type", 2)
        startActivity(intent)
    }
}

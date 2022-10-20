package com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.base.BaseFragment
import com.syntepro.appbeneficiosbolivia.core.base.viewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.entity.service.PresentDetail
import com.syntepro.appbeneficiosbolivia.ui.benefy.BenefyDetailActivity
import com.syntepro.appbeneficiosbolivia.ui.benefy.PromotionalCodeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.model.BannerResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.PurchasedProductsRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.PurchasedProductsResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.UserSavingsRequest
import com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters.BannerAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.ui.adapters.ProductsAdapter
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities.NotificationsActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.StatisticsActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.SuccessGiftUserActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.coupon_empty_layout.*
import kotlinx.android.synthetic.main.fragment_benefy.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class BenefyFragment: BaseFragment() {

    @Inject
    lateinit var bannerAdapter: BannerAdapter

    @Inject
    lateinit var productsAdapter: ProductsAdapter

    private lateinit var homeViewModel: HomeViewModel

    override fun layoutId(): Int = R.layout.fragment_benefy

    private lateinit var recyclerViewBanner: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private val format = DecimalFormat("###,##0.0#")
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        homeViewModel = viewModel(viewModelFactory) {
            observe(banners, ::handleBanners)
            observe(purchaseProducts, ::handlePurchaseProducts)
            observe(userSavings, ::handleUserSavings)
            failure(failure, ::handleFailure)
        }

        bannerAdapter.parentFragment(this)
        productsAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewBanner = view.findViewById(R.id.promotionalListId)

        initLists()

        if (!Functions.isDarkTheme(requireActivity()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            savingsCard.setCardBackgroundColor(requireContext().getColor(R.color.gray_card_benefit))

        promotional.setOnClickListener {
            val intent = Intent(requireContext(), PromotionalCodeActivity::class.java)
            intent.putExtra("allowAccess", true)
            this.startActivityForResult(intent, 175)
        }

        code.setOnClickListener { promotional.callOnClick() }

        savingsCard.setOnClickListener {
            val intent = Intent(requireContext(), StatisticsActivity::class.java)
            startActivity(intent)
        }

        recyclerViewBanner.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 1) stopAutoScrollBanner()
                else if (newState == 0) {
                    position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    runAutoScrollBanner()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
//        getPurchaseProducts()
//        getUserSavings()
    }

    override fun onResume() {
        super.onResume()
        runAutoScrollBanner()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScrollBanner()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 175 && resultCode == Activity.RESULT_OK) {
            data?.let {
                val code = it.getBooleanExtra("code", false)
                val model = it.getSerializableExtra("model") as? PresentDetail
                if (code) getPurchaseProducts()
                model?.let { pd ->
                    val intent = Intent(requireContext(), SuccessGiftUserActivity::class.java)
                    intent.putExtra("model", pd)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initLists() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewBanner.setHasFixedSize(true)
        recyclerViewBanner.layoutManager = layoutManager
        recyclerViewBanner.itemAnimator = DefaultItemAnimator()
        recyclerViewBanner.adapter = bannerAdapter

        // Purchased Recycler View
        couponsListId.setHasFixedSize(true)
        couponsListId.layoutManager = GridLayoutManager(requireContext(), 2)
        couponsListId.adapter = productsAdapter
    }

    private fun getPurchaseProducts() {
        showProgress()
        val request = PurchasedProductsRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = 1,
            recordsNumber = Constants.LIST_PAGE_SIZE,
            pageNumber = 1,
            sortType = 1,
            idUser = Constants.userProfile?.idUser ?: ""
        )
        homeViewModel.loadPurchaseProducts(request)
    }

    private fun getUserSavings() {
        val request = UserSavingsRequest(
            country = Constants.userProfile?.actualCountry ?: "BO",
            language = 1,
            idUserFirebase = Constants.userProfile?.idUserFirebase ?: ""
        )
        homeViewModel.loadUserSavings(request)
    }

    private fun handleBanners(response: BaseResponse<List<BannerResponse>>?) {
        bannerAdapter.collection = response?.data.orEmpty()
        response?.data?.let {
            if (it.isEmpty()) promotionalListId.visibility = View.GONE
            else promotionalListId.visibility = View.VISIBLE
        } ?: run {
            promotionalListId.visibility = View.GONE
        }
    }

    private fun handlePurchaseProducts(response: BaseResponse<List<PurchasedProductsResponse>>?) {
        hideProgress()
        if (response?.data.isNullOrEmpty()) emptyId.visibility = View.VISIBLE else emptyId.visibility = View.GONE
        productsAdapter.collection = response?.data.orEmpty()
    }

    private fun handleUserSavings(response: BaseResponse<Double>?) {
        val savings = "${Constants.userProfile?.currency ?: "Bs"} ${format.format(response?.data ?: 0.0)}"
        userSavingsId.text = savings
    }

    fun openBanner(item: Any) {
        val model = item as BannerResponse
        Functions.openURL(model.urlSite, requireContext())
    }

    fun openProduct(item: Any) {
        val model = item as PurchasedProductsResponse
        if (model.blocked) {
            val intent = Intent(requireContext(), PromotionalCodeActivity::class.java)
            intent.putExtra("allowAccess", false)
            intent.putExtra("commerceId", model.idComercio)
            intent.putExtra("productId", model.idProduct)
            this.startActivityForResult(intent, 175)
        } else {
            val intent = Intent(requireContext(), BenefyDetailActivity::class.java)
            intent.putExtra("productId", model.idPurchasedProductIndex)
            startActivity(intent)
        }
    }

    private fun stopAutoScrollBanner() {
        if (timer != null && timerTask != null) {
            timerTask!!.cancel()
            timer!!.cancel()
            timer = null
            timerTask = null
            position = layoutManager.findFirstCompletelyVisibleItemPosition()
        }
    }

    private fun runAutoScrollBanner() {
        if (timer == null && timerTask == null) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    if (position == Int.MAX_VALUE) {
                        position = Int.MAX_VALUE / 2
                        recyclerViewBanner.scrollToPosition(position)
                        recyclerViewBanner.smoothScrollBy(5, 0)
                    } else {
                        position++
                        recyclerViewBanner.smoothScrollToPosition(position)
                    }
                }
            }
            timer!!.schedule(timerTask, 5000, 5000)
        }
    }

    fun navigateToLoyalty() { Navigation.findNavController(requireView()).navigate(R.id.nav_lealtad) }

}
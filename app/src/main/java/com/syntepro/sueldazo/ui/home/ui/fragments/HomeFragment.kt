package com.syntepro.sueldazo.ui.home.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.core.base.BaseFragment
import com.syntepro.sueldazo.core.base.viewModel
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.home.adapter.GiftCardsAdapter
import com.syntepro.sueldazo.ui.home.model.*
import com.syntepro.sueldazo.ui.home.ui.adapters.ArticleAdapter
import com.syntepro.sueldazo.ui.home.ui.adapters.BannerAdapter
import com.syntepro.sueldazo.ui.home.ui.adapters.SavingsCategoryAdapter
import com.syntepro.sueldazo.ui.home.viewModel.HomeViewModel
import com.syntepro.sueldazo.ui.notifications.model.NotificationCountResponse
import com.syntepro.sueldazo.ui.shop.ui.activities.ShopDetailActivity
import com.syntepro.sueldazo.utils.Constants
import com.syntepro.sueldazo.utils.Functions
import com.syntepro.sueldazo.utils.PercentCustomFormatter
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    @Inject
    lateinit var articleAdapter: ArticleAdapter

    @Inject
    lateinit var bannerAdapter: BannerAdapter

    @Inject
    lateinit var savingsCategoryAdapter: SavingsCategoryAdapter

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
        savingsCategoryAdapter.parentFragment(this)
        giftCardsAdapter.parentFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerview()
        getBanners()
        configPieChart()
        getCategories()

    }

    private fun configPieChart(){
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)


        pieChart.dragDecelerationFrictionCoef = 0.95f

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.isDrawHoleEnabled = false
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(R.color.white)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.rotationAngle = 0f

        // enable rotation of the pieChart by touch
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(40f))
        entries.add(PieEntry(30f))
        entries.add(PieEntry(20f))
        entries.add(PieEntry(10f))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 0.5f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.chart_color_1))
        colors.add(resources.getColor(R.color.chart_color_2))
        colors.add(resources.getColor(R.color.chart_color_3))
        colors.add(resources.getColor(R.color.chart_color_4))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        val percentCustomFormatter = PercentCustomFormatter()
        percentCustomFormatter.PercentFormatter(pieChart)
        percentCustomFormatter.PercentFormatter()
        data.setValueFormatter(percentCustomFormatter)
//        data.setValueFormatter(DecimalFormat("##%"))
        data.setValueTextSize(12f)

        val pieChartFont = ResourcesCompat.getFont(requireContext(), R.font.worksans_regular)
        data.setValueTypeface(pieChartFont)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()

    }

    private fun configureRecyclerview() {
        bannerItemList.removeAllViewsInLayout()
        bannerItemList.adapter = null
        bannerItemList.setHasFixedSize(true)
        bannerItemList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        bannerItemList.itemAnimator = DefaultItemAnimator()
        bannerItemList.isLayoutFrozen = true
        bannerItemList.adapter = bannerAdapter

        categoryList.removeAllViewsInLayout()
        categoryList.adapter = null
        categoryList.setHasFixedSize(true)
        categoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        categoryList.itemAnimator = DefaultItemAnimator()
        categoryList.isLayoutFrozen = true
        categoryList.adapter = savingsCategoryAdapter
    }

    private fun getCategories(){
        val list = listOf(
            SavingCategory(categoryName = "Salud", color = "#F29849"),
            SavingCategory(categoryName = "Mascotas", color = "#07C4D9"),
            SavingCategory(categoryName = "Comida", color = "#9A8DF2"),
            SavingCategory(categoryName = "Hogar", color = "#F266CD")
        )
        savingsCategoryAdapter.collection = list
        savingsCategoryAdapter.notifyDataSetChanged()
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

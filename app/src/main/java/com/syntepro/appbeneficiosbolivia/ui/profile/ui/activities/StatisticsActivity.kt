package com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.mikephil.charting.data.BarEntry
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.ui.profile.model.UserStatsRequest
import com.syntepro.appbeneficiosbolivia.ui.profile.model.UserStatsResponse
import com.syntepro.appbeneficiosbolivia.ui.profile.viewModel.ProfileViewModel
import com.syntepro.appbeneficiosbolivia.utils.Constants.Companion.userProfile
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_statistics.*
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.*

class StatisticsActivity: BaseActivity(), AAChartView.AAChartViewCallBack {

    private lateinit var profileViewModel: ProfileViewModel
    private val roomDataBase: RoomDataBase by lazy { RoomDataBase.getRoomDatabase(this@StatisticsActivity) }
    private var aaChartModel = AAChartModel()
    private var xVals: MutableList<String>? = null
    private var yVals1: MutableList<BarEntry>? = null
    private var yVals2: MutableList<BarEntry>? = null
    private var firstTime = true
    private var i = 0
    var yearSelected = 0
    var monthSelected = 0
    var year = 0
    var month = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_statistics)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.mis_estadisticas)

        profileViewModel = viewModel(viewModelFactory) {
            observe(userStats, ::handleUserStats)
            failure(failure, ::handleError)
        }

        // Country User
        val cu = roomDataBase.accessDao().country
        txt_pais.text = cu.pais

        // Calendar Instance
        val date = Calendar.getInstance()
        year = date[Calendar.YEAR]
        yearSelected = date[Calendar.YEAR]
        month = date[Calendar.MONTH] + 1

        // Instance
        val listMonth: MutableList<String> = ArrayList()
        val months = DateFormatSymbols().months
        for (month in months) { listMonth.add(month.toUpperCase()) }
        val myAdapter = ArrayAdapter(this, R.layout.spinner_item, listMonth)
        sp_mes.adapter = myAdapter
        val mesActual = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        var indexmonth = 0
        for (i in listMonth.indices) {
            // Compara el mes actual con los textos desplegados en el spinner.
            if (mesActual?.toUpperCase() == myAdapter.getItem(i)?.toUpperCase() ?: "") indexmonth = i
        }
        sp_mes.setSelection(indexmonth)
        sp_mes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (month == position + 1) {
                    if (year == yearSelected) {
                        val p = getString(R.string.pendiente_c)
                        txt_p.text = p
                    } else {
                        val p = getString(R.string.perdida_p)
                        txt_p.text = p
                    }
                } else {
                    val p = getString(R.string.perdida_p)
                    txt_p.text = p
                }
                monthSelected = position + 1
                xVals = mutableListOf()
                yVals1 = mutableListOf()
                yVals2 = mutableListOf()
                clearData()
                if (!firstTime) loadUserStats(monthSelected, yearSelected)
                else firstTime = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }

        // Spinner year
        val years = ArrayList<String>()
        val thisYear = Calendar.getInstance()[Calendar.YEAR]
        for (i in 2018..thisYear) { years.add(i.toString()) }

        val adapter = ArrayAdapter(this, R.layout.spinner_item, years)
        sp_year.adapter = adapter
        sp_year.setSelection(adapter.count - 1)
        sp_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                yearSelected = adapterView.getItemAtPosition(i).toString().toInt()
                if (year != yearSelected) {
                    val p = getString(R.string.perdida_p)
                    txt_p.text = p
                } else if (month == monthSelected) {
                    val p = getString(R.string.pendiente_c)
                    txt_p.text = p
                }
                xVals = mutableListOf()
                yVals1 = mutableListOf()
                yVals2 = mutableListOf()
                clearData()
                loadUserStats(monthSelected, yearSelected)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {  }
        }

    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun loadUserStats(selectedMonth: Int, selectedYear: Int) {
        val request = UserStatsRequest(
                country = userProfile?.actualCountry ?: "BO",
                language = Functions.getLanguage(),
                idUser = userProfile?.idUser ?: "",
                month = selectedMonth,
                year = selectedYear
        )
        profileViewModel.getUserStats(request)
    }

    private fun handleUserStats(response: BaseResponse<UserStatsResponse>?) {
        response?.data?.let {
            val format = DecimalFormat("###,###.00")
            val monthSavings = "${userProfile?.currency ?: "Bs"} ${format.format(it.savings)}"
            txtahorro1.text = monthSavings
            val monthLoses = "${userProfile?.currency ?: "Bs"} ${format.format(it.loses)}"
            txt_perdida.text = monthLoses
            val months: MutableList<String> = mutableListOf()
            val savings: MutableList<Double> = mutableListOf()
            val loses: MutableList<Double> = mutableListOf()
            for (s in it.yearStats) {
                months.add(getMonthName(s.month).take(3))
                savings.add(s.stats.savings)
                loses.add(s.stats.loses)
//                xVals?.add(getMonthName(s.month))
//                yVals1!!.add(BarEntry(i++.toFloat(), s.stats.savings.toFloat()))
//                yVals2!!.add(BarEntry(i++.toFloat(), s.stats.loses.toFloat()))
            }
            val totalSavings = AASeriesElement()
                    .name(getString(R.string.ahorro_p))
                    .lineWidth(4f)
                    .step(true)
                    .color("#FE9305")
                    .data(savings.toTypedArray())

            val totalLoses = AASeriesElement()
                    .name(getString(R.string.perdida_p))
                    .lineWidth(4f)
                    .step(true)
                    .color("#D2DAE7")
                    .data(loses.toTypedArray())

            setUpAAChartView(months.toTypedArray(), arrayOf(totalSavings, totalLoses))
            configureLineChartAndSplineChartStyle()
//            setUpBarChart(xVals!!, yVals1, yVals2)
        }
    }

    private fun getMonthName(number: Int): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) Month.of(number).getDisplayName(TextStyle.FULL, Locale.US)
            else DateFormatSymbols().months[number].toString()
    }

    private fun setUpAAChartView(categories: Array<String>, data: Array<AASeriesElement>) {
        AAChartView.setBackgroundColor(0)
        AAChartView.background?.alpha = 0
        AAChartView.callBack = this
        aaChartModel = configureAAChartModel(categories, data)
        AAChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun configureAAChartModel(categories: Array<String>, data: Array<AASeriesElement>): AAChartModel {
        aaChartModel
                .categories(categories)
                .chartType(AAChartType.Spline)
                .yAxisGridLineWidth(0f)
                .legendEnabled(false)
                .touchEventEnabled(true)
//                .series(data)

        return aaChartModel
    }

    private fun configureLineChartAndSplineChartStyle() {
        aaChartModel
                .markerSymbolStyle(AAChartSymbolStyleType.BorderBlank)
                .markerRadius(6f)
                .animationType(AAChartAnimationType.SwingFromTo)
    }

//    private fun setUpBarChart(titles: MutableList<String>, saving: MutableList<BarEntry>?, lost: MutableList<BarEntry>?) {
//        val barWidth = 0.3f
//        val barSpace = 0f
//        val groupSpace = 0.4f
//
//        // Set the chart setting with the below following code
//        barChart.description = null
//        barChart.setPinchZoom(false)
//        barChart.setScaleEnabled(false)
//        barChart.setDrawBarShadow(false)
//        barChart.setDrawGridBackground(false)
//
//        // Next create the dummy data for display the graph
//        val groupCount = xVals!!.size
//
//        // Next, to draw the graph
//        val set1 = BarDataSet(saving, getString(R.string.ahorro_p))
//        set1.color = Color.rgb(254, 147, 5)
//        val set2 = BarDataSet(lost, getString(R.string.perdida_p))
//        set2.color = Color.rgb(210, 218, 231)
//        val data = BarData(set1, set2)
//        data.setValueFormatter(DefaultValueFormatter(2))
//        barChart.data = data
//        barChart.barData.barWidth = barWidth
//        barChart.xAxis.axisMinimum = 0f
//        barChart.xAxis.axisMaximum = 0 + barChart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
//        barChart.groupBars(0f, groupSpace, barSpace)
//        barChart.data.isHighlightEnabled = false
//        barChart.animateY(2000)
//        barChart.invalidate()
//
//        // Draw the indicator
//        val l = barChart.legend
//        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(true)
//        l.yOffset = 20f
//        l.xOffset = 0f
//        l.yEntrySpace = 0f
//        l.textSize = 8f
//
//        // Draw the X-Axis and Y-Axis
//        // X-axis
//        val xAxis = barChart.xAxis
//        xAxis.granularity = 1f
//        xAxis.isGranularityEnabled = true
//        xAxis.setCenterAxisLabels(true)
//        xAxis.setDrawGridLines(false)
//        xAxis.axisMaximum = titles.size.toFloat()
//        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
//        xAxis.valueFormatter = IndexAxisValueFormatter(titles)
//
//        // Y-axis
//        barChart.axisRight.isEnabled = false
//        val leftAxis = barChart.axisLeft
//        leftAxis.valueFormatter = LargeValueFormatter()
//        leftAxis.setDrawGridLines(true)
//        leftAxis.spaceTop = 35f
//        leftAxis.axisMinimum = 0f
//
//        // Get the primary text color of the theme
//        val typedValue = TypedValue()
//        val theme = this.theme
//        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
//        @SuppressLint("Recycle") val arr = this.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.textColorPrimary))
//        val primaryColor = arr.getColor(0, -1)
//
//        // Dark Theme
//        barChart.data.setValueTextColor(primaryColor)
//        set1.valueTextColor = primaryColor
//        set2.valueTextColor = primaryColor
//        xAxis.textColor = primaryColor
//        leftAxis.textColor = primaryColor
//    }

    private fun clearData() {
        xVals!!.clear()
        yVals1!!.clear()
        yVals2!!.clear()
        i = 1
    }

    override fun chartViewDidFinishLoad(aaChartView: AAChartView) {
        Log.e("Chart", "Finish")
    }

    override fun chartViewMoveOverEventMessage(aaChartView: AAChartView, messageModel: AAMoveOverEventMessageModel) {
        Log.e("Chart", "Finish")
    }

}
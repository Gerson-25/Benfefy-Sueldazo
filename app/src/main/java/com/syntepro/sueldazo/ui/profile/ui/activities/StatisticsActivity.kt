package com.syntepro.sueldazo.ui.profile.ui.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.merckers.core.extension.failure
import com.merckers.core.extension.observe
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.base.BaseActivity
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.room.database.RoomDataBase
import com.syntepro.sueldazo.ui.profile.model.UserStatsRequest
import com.syntepro.sueldazo.ui.profile.model.UserStatsResponse
import com.syntepro.sueldazo.ui.profile.viewModel.ProfileViewModel
import com.syntepro.sueldazo.utils.Constants.Companion.userProfile
import com.syntepro.sueldazo.utils.Functions
import kotlinx.android.synthetic.main.activity_statistics.*
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.*

class StatisticsActivity: BaseActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    lateinit var pieChart: PieChart
    private val roomDataBase: RoomDataBase by lazy { RoomDataBase.getRoomDatabase(this@StatisticsActivity) }
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

        pieChart = findViewById(R.id.pieChart)
        configPieChart()

//        // Toolbar
//        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
//        setSupportActionBar(myToolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//        supportActionBar!!.title = resources.getString(R.string.mis_estadisticas)

        profileViewModel = viewModel(viewModelFactory) {
            observe(userStats, ::handleUserStats)
            failure(failure, ::handleError)
        }

        // Country User
//        val cu = roomDataBase.accessDao().country
//        txt_pais.text = cu.pais

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

    private fun configPieChart(){
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)


        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(70f))
        entries.add(PieEntry(20f))
        entries.add(PieEntry(10f))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.blue))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()

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
        }
    }

    private fun getMonthName(number: Int): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) Month.of(number).getDisplayName(TextStyle.FULL, Locale.US)
            else DateFormatSymbols().months[number].toString()
    }


    private fun clearData() {
        xVals!!.clear()
        yVals1!!.clear()
        yVals2!!.clear()
        i = 1
    }

}
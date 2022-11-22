package com.syntepro.sueldazo.utils

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

public class PercentCustomFormatter: ValueFormatter() {
    var mFormat: DecimalFormat? = null
    private var pieChart: PieChart? = null

    fun PercentFormatter() {
        mFormat = DecimalFormat("###,###,##0.0")
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    fun PercentFormatter(pieChart: PieChart?) {
        this.pieChart = pieChart
    }

    override fun getFormattedValue(value: Float): String? {
        return mFormat!!.format(value.toDouble()) + " %"
    }

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String? {
        return if (pieChart != null && pieChart!!.isUsePercentValuesEnabled) {
            // Converted to percent
            getFormattedValue(value)
        } else {
            // raw value, skip percent sign
            mFormat!!.format(value.toDouble())
        }
    }
}
package com.laimburggasse.aquariumcontrol.timedata.presentation

import android.annotation.SuppressLint
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants

class TimeDataYAxisDegreeCelsiusValueFormatter : ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (axis is YAxis) {
            return String.format("%3.1f", value) + GlobalConstants.LABEL_UNIT_DEGREE_CELSIUS
        }
        return ""
    }
}
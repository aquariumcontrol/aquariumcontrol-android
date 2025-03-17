package com.laimburggasse.aquariumcontroller.timedata

import android.annotation.SuppressLint
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter

class TimeDataYAxisOnOffValueFormatter: ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (axis is YAxis) {
            return if (value > 0.0) "ON" else "OFF"
        }
        return ""
    }
}
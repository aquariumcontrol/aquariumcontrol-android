package com.laimburggasse.aquariumcontrol.timedata.presentation

import android.annotation.SuppressLint
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants

class TimeDataYAxisOnOffValueFormatter: ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (axis is YAxis) {
            return if (value > 0.0) GlobalConstants.LABEL_UNIT_ON else GlobalConstants.LABEL_UNIT_OFF
        }
        return ""
    }
}
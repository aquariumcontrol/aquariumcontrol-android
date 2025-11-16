package com.laimburggasse.aquariumcontrol.timedata.presentation

import android.annotation.SuppressLint
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants

class TimeDataYAxisMicroSievertPerCentimeterValueFormatter: ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (axis is YAxis) {
            return String.format("%2.0f", value/1000f) + GlobalConstants.LABEL_UNIT_MICROSIEVERT_PER_CM
        }
        return ""
    }
}
package com.example.aquariumcontroller.timedata

import android.annotation.SuppressLint
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class TimeDataXAxisValueFormatter : ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (axis is XAxis) {
            val millis: Long = value.toLong() * 1000
            val timeMillis = Date(millis)
            val dateTimeFormat: DateFormat =
                DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault())

            return dateTimeFormat.format(timeMillis)
        }
        return ""
    }
}
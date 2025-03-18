package com.laimburggasse.aquariumcontrol.feed.edit

import android.annotation.SuppressLint
import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter

class FeedProfileAxisValueFormatter(private var totalDuration: Float) : ValueFormatter() {
    // check if the chart value is within the intervals dedicated to each actuator
    private fun isFeederChartValue(value: Float): Boolean {
        return (value > FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF -
                FeedProfileDisplayYAxisValueDefinition.TOLERANCE) &&
                (value < FeedProfileDisplayYAxisValueDefinition.FEEDER_ON +
                        FeedProfileDisplayYAxisValueDefinition.TOLERANCE)
    }

    private fun isSkimmerChartValue(value: Float): Boolean {
        return (value > FeedProfileDisplayYAxisValueDefinition.SKIMMER_OFF -
                FeedProfileDisplayYAxisValueDefinition.TOLERANCE) &&
                (value < FeedProfileDisplayYAxisValueDefinition.SKIMMER_ON +
                        FeedProfileDisplayYAxisValueDefinition.TOLERANCE)
    }

    private fun isMainPump1ChartValue(value: Float): Boolean {
        return (value > FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_OFF -
                FeedProfileDisplayYAxisValueDefinition.TOLERANCE) &&
                (value < FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_ON +
                        FeedProfileDisplayYAxisValueDefinition.TOLERANCE)
    }

    private fun isMainPump2ChartValue(value: Float): Boolean {
        return (value > FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_OFF -
                FeedProfileDisplayYAxisValueDefinition.TOLERANCE) &&
                (value < FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_ON +
                        FeedProfileDisplayYAxisValueDefinition.TOLERANCE)
    }

    private fun isAuxPump1ChartValue(value: Float): Boolean {
        return (value > FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_OFF -
                FeedProfileDisplayYAxisValueDefinition.TOLERANCE) &&
                (value < FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_ON +
                        FeedProfileDisplayYAxisValueDefinition.TOLERANCE)
    }

    private fun isAuxPump2ChartValue(value: Float): Boolean {
        return (value > FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_OFF -
                FeedProfileDisplayYAxisValueDefinition.TOLERANCE) &&
                (value < FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_ON +
                        FeedProfileDisplayYAxisValueDefinition.TOLERANCE)
    }

    @SuppressLint("DefaultLocale")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (axis is YAxis) {
            Log.d("FeedProfileAxisValueFormatter", "getAxisLabel: $value")
            if (isFeederChartValue(value)) {
                return if ((value > FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF +
                            FeedProfileDisplayYAxisValueDefinition.THRESHOLD)
                ) "ON" else "OFF"
            }
            if (isSkimmerChartValue(value)) {
                return if ((value > FeedProfileDisplayYAxisValueDefinition.SKIMMER_OFF +
                            FeedProfileDisplayYAxisValueDefinition.THRESHOLD)
                ) "ON" else "OFF"
            }
            if (isMainPump1ChartValue(value)) {
                return if ((value > FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_OFF +
                            FeedProfileDisplayYAxisValueDefinition.THRESHOLD)
                ) "ON" else "OFF"
            }
            if (isMainPump2ChartValue(value)) {
                return if ((value > FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_OFF +
                            FeedProfileDisplayYAxisValueDefinition.THRESHOLD)
                ) "ON" else "OFF"
            }
            if (isAuxPump1ChartValue(value)) {
                return if ((value > FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_OFF +
                            FeedProfileDisplayYAxisValueDefinition.THRESHOLD)
                ) "ON" else "OFF"
            }
            if (isAuxPump2ChartValue(value)) {
                return if ((value > FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_OFF +
                            FeedProfileDisplayYAxisValueDefinition.THRESHOLD)
                ) "ON" else "OFF"
            }
        }
        if (axis is XAxis) {
            // dynamic precision of label
            if (totalDuration < 0.1) {
                return String.format("%2.2f", value)
            }
            if (totalDuration < 1) {
                return String.format("%2.1f", value)
            }
            return String.format("%2.0f", value)
        }
        return ""
    }
}

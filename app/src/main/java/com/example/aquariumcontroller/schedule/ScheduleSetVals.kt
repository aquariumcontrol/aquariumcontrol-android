package com.example.aquariumcontroller.schedule

import android.annotation.SuppressLint
import com.example.aquariumcontroller.ApiParams
import java.util.Locale

@SuppressLint("SimpleDateFormat")

class ScheduleSetVals(
    var ballingStartTime: Float,
    var ballingStopTime: Float,
    var ballingLimitationActive: Boolean,
    var refillStartTime: Float,
    var refillStopTime: Float,
    var refillLimitationActive: Boolean,
    var ventilationStartTime: Float,
    var ventilationStopTime: Float,
    var ventilationLimitationActive: Boolean,
    var heatingStartTime: Float,
    var heatingStopTime: Float,
    var heatingLimitationActive: Boolean,
) {
    fun updateFromViewModels(
        ballingSetVals: ScheduleSetValsViewModel,
        refillSetVals: ScheduleSetValsViewModel,
        ventilationSetVals: ScheduleSetValsViewModel,
        heatingSetVals: ScheduleSetValsViewModel
    ) {
        ballingStartTime = ballingSetVals.getStartTime()
        ballingStopTime = ballingSetVals.getStopTime()
        ballingLimitationActive = ballingSetVals.isLimitationActive()
        refillStartTime = refillSetVals.getStartTime()
        refillStopTime = refillSetVals.getStopTime()
        refillLimitationActive = refillSetVals.isLimitationActive()
        ventilationStartTime = ventilationSetVals.getStartTime()
        ventilationStopTime = ventilationSetVals.getStopTime()
        ventilationLimitationActive = ventilationSetVals.isLimitationActive()
        heatingStartTime = heatingSetVals.getStartTime()
        heatingStopTime = heatingSetVals.getStopTime()
        heatingLimitationActive = heatingSetVals.isLimitationActive()
    }

    private fun formatTime(value: Float): String {
        val hours = value.toInt()
        val minutes = ((value - hours) * 60).toInt()
        return String.format(locale = Locale.US, "%02d:%02d", hours, minutes)
    }

    fun getApiParams(): Map<String, String> {
        return mapOf(
            ApiParams.SCHEDULE_BALLING_START_TIME to formatTime(ballingStartTime),
            ApiParams.SCHEDULE_BALLING_STOP_TIME to formatTime(ballingStopTime),
            ApiParams.SCHEDULE_BALLING_IS_ACTIVE to if(ballingLimitationActive) "1" else "0",
            ApiParams.SCHEDULE_REFILL_START_TIME to formatTime(refillStartTime),
            ApiParams.SCHEDULE_REFILL_STOP_TIME to formatTime(refillStopTime),
            ApiParams.SCHEDULE_REFILL_IS_ACTIVE to if(refillLimitationActive) "1" else "0",
            ApiParams.SCHEDULE_VENTILATION_START_TIME to formatTime(ventilationStartTime),
            ApiParams.SCHEDULE_VENTILATION_STOP_TIME to formatTime(ventilationStopTime),
            ApiParams.SCHEDULE_VENTILATION_IS_ACTIVE to if(ventilationLimitationActive) "1" else "0",
            ApiParams.SCHEDULE_HEATING_START_TIME to formatTime(heatingStartTime),
            ApiParams.SCHEDULE_HEATING_STOP_TIME to formatTime(heatingStopTime),
            ApiParams.SCHEDULE_HEATING_IS_ACTIVE to if(heatingLimitationActive) "1" else "0",
        )
    }
}
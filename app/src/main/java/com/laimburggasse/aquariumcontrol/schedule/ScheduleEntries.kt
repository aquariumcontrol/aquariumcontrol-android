package com.laimburggasse.aquariumcontrol.schedule

import android.annotation.SuppressLint
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import java.util.Locale

@SuppressLint("SimpleDateFormat")

class ScheduleEntries(
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
        ballingScheduleEntryViewModel: ScheduleEntryViewModel,
        refillScheduleEntryViewModel: ScheduleEntryViewModel,
        ventilationScheduleEntryViewModel: ScheduleEntryViewModel,
        heatingScheduleEntryViewModel: ScheduleEntryViewModel
    ) {
        ballingStartTime = ballingScheduleEntryViewModel.getStartTime()
        ballingStopTime = ballingScheduleEntryViewModel.getStopTime()
        ballingLimitationActive = ballingScheduleEntryViewModel.isLimitationActive()
        refillStartTime = refillScheduleEntryViewModel.getStartTime()
        refillStopTime = refillScheduleEntryViewModel.getStopTime()
        refillLimitationActive = refillScheduleEntryViewModel.isLimitationActive()
        ventilationStartTime = ventilationScheduleEntryViewModel.getStartTime()
        ventilationStopTime = ventilationScheduleEntryViewModel.getStopTime()
        ventilationLimitationActive = ventilationScheduleEntryViewModel.isLimitationActive()
        heatingStartTime = heatingScheduleEntryViewModel.getStartTime()
        heatingStopTime = heatingScheduleEntryViewModel.getStopTime()
        heatingLimitationActive = heatingScheduleEntryViewModel.isLimitationActive()
    }

    private fun formatTime(value: Float): String {
        val hours = value.toInt()
        val minutes = ((value - hours) * 60).toInt()
        return String.format(locale = Locale.US, "%02d:%02d", hours, minutes)
    }

    fun getApiParams(): Map<String, String> {
        return mapOf(
            WebApiParams.SCHEDULE_BALLING_START_TIME to formatTime(ballingStartTime),
            WebApiParams.SCHEDULE_BALLING_STOP_TIME to formatTime(ballingStopTime),
            WebApiParams.SCHEDULE_BALLING_IS_ACTIVE to if(ballingLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_REFILL_START_TIME to formatTime(refillStartTime),
            WebApiParams.SCHEDULE_REFILL_STOP_TIME to formatTime(refillStopTime),
            WebApiParams.SCHEDULE_REFILL_IS_ACTIVE to if(refillLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_VENTILATION_START_TIME to formatTime(ventilationStartTime),
            WebApiParams.SCHEDULE_VENTILATION_STOP_TIME to formatTime(ventilationStopTime),
            WebApiParams.SCHEDULE_VENTILATION_IS_ACTIVE to if(ventilationLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_HEATING_START_TIME to formatTime(heatingStartTime),
            WebApiParams.SCHEDULE_HEATING_STOP_TIME to formatTime(heatingStopTime),
            WebApiParams.SCHEDULE_HEATING_IS_ACTIVE to if(heatingLimitationActive) "1" else "0",
        )
    }
}
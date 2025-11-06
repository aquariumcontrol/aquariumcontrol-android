package com.laimburggasse.aquariumcontrol.schedule.domain.model

import android.annotation.SuppressLint
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import java.util.Locale

@SuppressLint("SimpleDateFormat")

data class ScheduleEntries(
    var ballingStartTime: Float,
    var ballingStopTime: Float,
    var ballingLimitationActive: Boolean,
    var ballingScheduleLoaded: Boolean,
    var refillStartTime: Float,
    var refillStopTime: Float,
    var refillLimitationActive: Boolean,
    var refillScheduleLoaded: Boolean,
    var ventilationStartTime: Float,
    var ventilationStopTime: Float,
    var ventilationLimitationActive: Boolean,
    var ventilationScheduleLoaded: Boolean,
    var heatingStartTime: Float,
    var heatingStopTime: Float,
    var heatingLimitationActive: Boolean,
    var heatingScheduleLoaded: Boolean
) {
    private fun formatTime(value: Float): String {
        val hours: Int = value.toInt()
        val minutes: Int = ((value - hours) * 60).toInt()
        return String.format(locale = Locale.US, "%02d:%02d", hours, minutes)
    }

    fun getApiParams(): Map<String, String> {
        return mapOf(
            WebApiParams.SCHEDULE_BALLING_START_TIME to formatTime(value = ballingStartTime),
            WebApiParams.SCHEDULE_BALLING_STOP_TIME to formatTime(value = ballingStopTime),
            WebApiParams.SCHEDULE_BALLING_IS_ACTIVE to if(ballingLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_REFILL_START_TIME to formatTime(value = refillStartTime),
            WebApiParams.SCHEDULE_REFILL_STOP_TIME to formatTime(value = refillStopTime),
            WebApiParams.SCHEDULE_REFILL_IS_ACTIVE to if(refillLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_VENTILATION_START_TIME to formatTime(value = ventilationStartTime),
            WebApiParams.SCHEDULE_VENTILATION_STOP_TIME to formatTime(value = ventilationStopTime),
            WebApiParams.SCHEDULE_VENTILATION_IS_ACTIVE to if(ventilationLimitationActive) "1" else "0",
            WebApiParams.SCHEDULE_HEATING_START_TIME to formatTime(value = heatingStartTime),
            WebApiParams.SCHEDULE_HEATING_STOP_TIME to formatTime(value = heatingStopTime),
            WebApiParams.SCHEDULE_HEATING_IS_ACTIVE to if(heatingLimitationActive) "1" else "0",
        )
    }
}
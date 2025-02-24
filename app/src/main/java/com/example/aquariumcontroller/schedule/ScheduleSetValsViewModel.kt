package com.example.aquariumcontroller.schedule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.ApiParams
import java.util.Locale

class ScheduleSetValsViewModel(
    private var scheduleSetVals: ScheduleSetVals,
) : ViewModel() {
    // This class will allow the user to edit the schedule for
    // - Balling
    // - Refill
    // - Ventilation
    // - Heating

    private val _sliderRangeBalling = MutableLiveData<ClosedFloatingPointRange<Float>>()
    private val _lowerBoundValueBalling = MutableLiveData<Float>()
    private val _upperBoundValueBalling = MutableLiveData<Float>()
    val sliderRange: LiveData<ClosedFloatingPointRange<Float>> = _sliderRangeBalling
    val lowerBoundValue: LiveData<Float> = _lowerBoundValueBalling
    val upperBoundValue: LiveData<Float> = _upperBoundValueBalling

    val onRangeChangedBySlider: (Float, Float) -> Unit = { start, end ->
        Log.d("ScheduleSetValsViewModel", "onRangeChangedBySlider: $start - $end")
        _sliderRangeBalling.value = start..end
        _lowerBoundValueBalling.value = start
        _upperBoundValueBalling.value = end
    }

    val onLowerValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRangeBalling.value
        if (currentRange != null && value < currentRange.endInclusive) {
            Log.d("ScheduleSetValsViewModel", "onLowerValueChanged: $value")
            _sliderRangeBalling.value = value..currentRange.endInclusive
            _lowerBoundValueBalling.value = value
        }
    }

    val onUpperValueChanged: (Float) -> Unit = { value ->
        val currentRange = _sliderRangeBalling.value
        if (currentRange != null && value > currentRange.start) {
            Log.d("ScheduleSetValsViewModel", "onUpperValueChanged: $value")
            _sliderRangeBalling.value = currentRange.start..value
            _upperBoundValueBalling.value = value
        }
    }

    fun resetValues() {
        _sliderRangeBalling.value =
            scheduleSetVals.ballingStartTime.toFloat()..
                    scheduleSetVals.ballingStopTime.toFloat()

        _lowerBoundValueBalling.value = scheduleSetVals.ballingStartTime
        _upperBoundValueBalling.value = scheduleSetVals.ballingStopTime
    }

    fun updateValues() {
        if (_sliderRangeBalling.value != null) {
            scheduleSetVals.ballingStartTime = sliderRange.value!!.start
            scheduleSetVals.ballingStopTime = sliderRange.value!!.endInclusive
        } else {
            Log.d("ScheduleSetValsViewModel", "updateValues: _sliderRangeBalling.value is null")
        }
    }

    fun setValues(newScheduleSetVals: ScheduleSetVals) {
        this.scheduleSetVals.ballingStartTime = newScheduleSetVals.ballingStartTime
        this.scheduleSetVals.ballingStopTime = newScheduleSetVals.ballingStopTime
        _sliderRangeBalling.value =
            this.scheduleSetVals.ballingStartTime..this.scheduleSetVals.ballingStopTime
        _lowerBoundValueBalling.value = scheduleSetVals.ballingStartTime
        _upperBoundValueBalling.value = scheduleSetVals.ballingStopTime
    }

    fun formatTime(value: Float): String {
        val hours = value.toInt()
        val minutes = ((value - hours) * 60).toInt()
        return String.format(locale = Locale.US, "%02d:%02d", hours, minutes)
    }

    fun getBallingStartTimeString(): String {
        Log.d(
            "ScheduleSetValsViewModel",
            "getBallingStartTimeString: ${scheduleSetVals.ballingStartTime}"
        )
        return formatTime(scheduleSetVals.ballingStartTime)
    }

    fun getBallingStopTimeString(): String {
        Log.d(
            "ScheduleSetValsViewModel",
            "getBallingStopTimeString: ${scheduleSetVals.ballingStopTime}"
        )
        return formatTime(scheduleSetVals.ballingStopTime)
    }

    fun getApiParams(): Map<String, String> {
        return mapOf(
            ApiParams.SCHEDULE_BALLING_START_TIME to getBallingStartTimeString(),
            ApiParams.SCHEDULE_BALLING_STOP_TIME to getBallingStopTimeString(),
            ApiParams.SCHEDULE_BALLING_IS_ACTIVE to scheduleSetVals.ballingLimitationActive.toString(),
            /* ApiParams.SCHEDULE_REFILL_START_TIME to getRefillStartTimeString(),
            ApiParams.SCHEDULE_REFILL_STOP_TIME to getRefillStopTimeString(),
            ApiParams.SCHEDULE_REFILL_IS_ACTIVE to scheduleSetVals.refillLimitationActive.toString()
            ApiParams.SCHEDULE_VENTILATION_START_TIME to getVentilationStartTimeString(),
            ApiParams.SCHEDULE_VENTILATION_STOP_TIME to getVentilationStopTimeString(),
            ApiParams.SCHEDULE_VENTILATION_IS_ACTIVE to scheduleSetVals.ventilationLimitationActive.toString()
            ApiParams.SCHEDULE_HEATING_START_TIME to getHeatingStartTimeString(),
            ApiParams.SCHEDULE_HEATING_STOP_TIME to getHeatingStopTimeString(),
            ApiParams.SCHEDULE_HEATING_IS_ACTIVE to scheduleSetVals.heatingLimitationActive.toString()*/
        )
    }
}
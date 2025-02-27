package com.example.aquariumcontroller.schedule

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName

@SuppressLint("SimpleDateFormat")

class ScheduleSetVals(
    @SerialName("BallingRangeStartTime") var ballingStartTime: Float,
    @SerialName("BallingRangeFinishTime") var ballingStopTime: Float,
    @SerialName("BallingRangeIsActive") var ballingLimitationActive: Boolean,
    @SerialName("RefillRangeStartTime") var refillStartTime: Float,
    @SerialName("RefillRangeFinishTime") var refillStopTime: Float,
    @SerialName("RefillRangeIsActive")var refillLimitationActive: Boolean,
    @SerialName("VentilationRangeStartTime") var ventilationStartTime: Float,
    @SerialName("VentilationRangeFinishTime") var ventilationStopTime: Float,
    @SerialName("VentilationRangeIsActive") var ventilationLimitationActive: Boolean,
    @SerialName("HeatingRangeStartTime") var heatingStartTime: Float,
    @SerialName("HeatingRangeFinishTime") var heatingStopTime: Float,
    @SerialName("HeatingRangeIsActive") var heatingLimitationActive: Boolean,
)
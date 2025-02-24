package com.example.aquariumcontroller.schedule

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import java.sql.Time
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")

fun getInitialStartTime(): Time {
    val sdf = SimpleDateFormat("HH:mm:ss")
    val date = sdf.parse("00:00:00")
    return Time(date!!.time)
}

@SuppressLint("SimpleDateFormat")
fun getInitialStopTime(): Time {
    val sdf = SimpleDateFormat("HH:mm:ss")
    val date = sdf.parse("23:59:59")
    return Time(date!!.time)
}

class ScheduleSetVals(
    @SerialName("BallingRangeStartTime") var ballingStartTime: Float,
    @SerialName("BallingRangeFinishTime") var ballingStopTime: Float,
    @SerialName("BallingRangeIsActive") var ballingLimitationActive: Boolean
) {
    @SerialName("RefillRangeStartTime")
    var refillStartTime: Time = getInitialStartTime()

    @SerialName("RefillRangeFinishTime")
    var refillStopTime: Time = getInitialStopTime()

    @SerialName("RefillRangeIsActive")
    var refillLimitationActive: Boolean = false

    @SerialName("VentilationRangeStartTime")
    var ventilationStartTime: Time = getInitialStartTime()

    @SerialName("VentilationRangeFinishTime")
    var ventilationStopTime: Time = getInitialStopTime()

    @SerialName("VentilationRangeIsActive")
    var ventilationLimitationActive: Boolean = false

    @SerialName("HeatingRangeStartTime")
    var heatingStartTime: Time = getInitialStartTime()

    @SerialName("HeatingRangeFinishTime")
    var heatingStopTime: Time = getInitialStopTime()

    @SerialName("HeatingRangeIsActive")
    var heatingLimitationActive: Boolean = false
}
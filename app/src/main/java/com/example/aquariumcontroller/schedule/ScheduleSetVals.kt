package com.example.aquariumcontroller.schedule

import android.annotation.SuppressLint

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
)
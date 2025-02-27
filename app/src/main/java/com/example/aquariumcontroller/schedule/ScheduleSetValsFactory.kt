package com.example.aquariumcontroller.schedule

import android.util.Log

class ScheduleSetValsFactory (
    private val scheduleSetVals: List<ScheduleSetVal>
) {
    // This class creates an instance of ScheduleSetVals
    // Different from the other factory classes, this one shall provide multiple instances.

    fun create(): ScheduleSetVals {
        // initialize with default values
        var ballingStartTime = 0f
        var ballingStopTime = 24f
        var ballingLimitationActive = false
        var refillStartTime = 0f
        var refillStopTime = 24f
        var refillLimitationActive = false
        var ventilationStartTime = 0f
        var ventilationStopTime = 24f
        var ventilationLimitationActive = false
        var heatingStartTime = 0f
        var heatingStopTime = 24f
        var heatingLimitationActive = false
        
        if (scheduleSetVals.isNotEmpty()) {
            for (scheduleSetVal in scheduleSetVals) {
                Log.d("ScheduleSetValsFactory", "parsing scheduleSetVal: ${scheduleSetVal.scheduleType}")
                when (scheduleSetVal.scheduleType) {
                    ScheduleType.BALLING.key -> {
                        ballingStartTime = scheduleSetVal.startTime
                        ballingStopTime = scheduleSetVal.stopTime
                        ballingLimitationActive = scheduleSetVal.isLimitationActive
                    }

                    ScheduleType.REFILL.key -> {
                        refillStartTime = scheduleSetVal.startTime
                        refillStopTime = scheduleSetVal.stopTime
                        refillLimitationActive = scheduleSetVal.isLimitationActive
                    }

                    ScheduleType.VENTILATION.key-> {
                        ventilationStartTime = scheduleSetVal.startTime
                        ventilationStopTime = scheduleSetVal.stopTime
                        ventilationLimitationActive = scheduleSetVal.isLimitationActive
                    }

                    ScheduleType.HEATING.key -> {
                        heatingStartTime = scheduleSetVal.startTime
                        heatingStopTime = scheduleSetVal.stopTime
                        heatingLimitationActive = scheduleSetVal.isLimitationActive
                    }
                }
            }
        }

        Log.d("ScheduleSetValsFactory", "ballingStartTime: $ballingStartTime")
        Log.d("ScheduleSetValsFactory", "ballingStopTime: $ballingStopTime")
        Log.d("ScheduleSetValsFactory", "ballingLimitationActive: $ballingLimitationActive")
        Log.d("ScheduleSetValsFactory", "refillStartTime: $refillStartTime")
        Log.d("ScheduleSetValsFactory", "refillStopTime: $refillStopTime")
        Log.d("ScheduleSetValsFactory", "refillLimitationActive: $refillLimitationActive")
        Log.d("ScheduleSetValsFactory", "ventilationStartTime: $ventilationStartTime")
        Log.d("ScheduleSetValsFactory", "ventilationStopTime: $ventilationStopTime")
        Log.d("ScheduleSetValsFactory", "ventilationLimitationActive: $ventilationLimitationActive")
        Log.d("ScheduleSetValsFactory", "heatingStartTime: $heatingStartTime")
        Log.d("ScheduleSetValsFactory", "heatingStopTime: $heatingStopTime")
        Log.d("ScheduleSetValsFactory", "heatingLimitationActive: $heatingLimitationActive")

        return ScheduleSetVals(
            ballingStartTime = ballingStartTime,
            ballingStopTime = ballingStopTime,
            ballingLimitationActive = ballingLimitationActive,
            refillStartTime = refillStartTime,
            refillStopTime = refillStopTime,
            refillLimitationActive = refillLimitationActive,
            ventilationStartTime = ventilationStartTime,
            ventilationStopTime = ventilationStopTime,
            ventilationLimitationActive = ventilationLimitationActive,
            heatingStartTime = heatingStartTime,
            heatingStopTime = heatingStopTime,
            heatingLimitationActive = heatingLimitationActive
        )
    }
}
package com.laimburggasse.aquariumcontrol.schedule

class ScheduleEntriesFactory (
    private val scheduleEntryList: List<ScheduleEntry>
) {
    // This class creates an instance of ScheduleSetVals
    // Different from the other factory classes, this one shall provide multiple instances.

    fun create(): ScheduleEntries {
        // initialize with default values
        var ballingStartTime = 0f
        var ballingStopTime = 24f
        var ballingLimitationActive = false
        var ballingScheduleLoaded = false
        var refillStartTime = 0f
        var refillStopTime = 24f
        var refillLimitationActive = false
        var refillScheduleLoaded = false
        var ventilationStartTime = 0f
        var ventilationStopTime = 24f
        var ventilationLimitationActive = false
        var ventilationScheduleLoaded = false
        var heatingStartTime = 0f
        var heatingStopTime = 24f
        var heatingLimitationActive = false
        var heatingScheduleLoaded = false
        
        if (scheduleEntryList.isNotEmpty()) {
            for (scheduleSetVal in scheduleEntryList) {
                when (scheduleSetVal.scheduleType) {
                    ScheduleType.BALLING.key -> {
                        ballingStartTime = scheduleSetVal.startTime
                        ballingStopTime = scheduleSetVal.stopTime
                        ballingLimitationActive = scheduleSetVal.isLimitationActive
                        ballingScheduleLoaded = true
                    }

                    ScheduleType.REFILL.key -> {
                        refillStartTime = scheduleSetVal.startTime
                        refillStopTime = scheduleSetVal.stopTime
                        refillLimitationActive = scheduleSetVal.isLimitationActive
                        refillScheduleLoaded = true
                    }

                    ScheduleType.VENTILATION.key-> {
                        ventilationStartTime = scheduleSetVal.startTime
                        ventilationStopTime = scheduleSetVal.stopTime
                        ventilationLimitationActive = scheduleSetVal.isLimitationActive
                        ventilationScheduleLoaded = true
                    }

                    ScheduleType.HEATING.key -> {
                        heatingStartTime = scheduleSetVal.startTime
                        heatingStopTime = scheduleSetVal.stopTime
                        heatingLimitationActive = scheduleSetVal.isLimitationActive
                        heatingScheduleLoaded = true
                    }
                }
            }
        }

        return ScheduleEntries(
            ballingStartTime,
            ballingStopTime,
            ballingLimitationActive,
            ballingScheduleLoaded,
            refillStartTime,
            refillStopTime,
            refillLimitationActive,
            refillScheduleLoaded,
            ventilationStartTime,
            ventilationStopTime,
            ventilationLimitationActive,
            ventilationScheduleLoaded,
            heatingStartTime,
            heatingStopTime,
            heatingLimitationActive,
            heatingScheduleLoaded
        )
    }
}
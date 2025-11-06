package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfilePhase
import kotlin.math.abs

object FeedProfileChartDataFactory {
    private data class FeedProfileChartLineDefinition(
        val name: String,
        val signal: FeedProfileSignal,
        val getValue: (FeedProfilePhase) -> Boolean,
        val yAxisValueOn: Float,
        val yAxisValueOff: Float
    )

    fun create(feedProfile: FeedProfile): FeedProfileChartDataBundle {
        val chartLines = listOf(
            FeedProfileChartLineDefinition("Feeder", FeedProfileSignal.FEEDER,
                getValue = { phase -> !phase.isPause },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.FEEDER_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF),
            FeedProfileChartLineDefinition("Skimmer", FeedProfileSignal.SKIMMER,
                getValue = { phase -> phase.skimmer },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.SKIMMER_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.SKIMMER_OFF),
            // ... Add definitions for MainPump1, MainPump2, AuxPump1, AuxPump2
            FeedProfileChartLineDefinition("Main Pump 1", FeedProfileSignal.MAIN_PUMP1,
                getValue = { phase -> phase.mainPump1 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_OFF),
            FeedProfileChartLineDefinition("Main Pump 2", FeedProfileSignal.MAIN_PUMP2,
                getValue = { phase -> phase.mainPump2 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_OFF),
            FeedProfileChartLineDefinition("Aux. Pump 1", FeedProfileSignal.AUX_PUMP1,
                getValue = { phase -> phase.auxPump1 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_OFF),
            FeedProfileChartLineDefinition("Aux. Pump 2", FeedProfileSignal.AUX_PUMP2,
                getValue = { phase -> phase.auxPump2 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_OFF)
        )

        val valuesMap: Map<FeedProfileChartLineDefinition, MutableList<Entry>> = chartLines.associateWith { mutableListOf() }
        var cumulatedTime = 0.0f
        val firstPhase: FeedProfilePhase = feedProfile.feedProfilePhases.firstOrNull() ?:
            return FeedProfileChartDataBundle(
                lineDataFeeder = null,
                lineDataSkimmer = null,
                lineDataMainPump1 = null,
                lineDataMainPump2 = null,
                lineDataAuxPump1 = null,
                lineDataAuxPump2 = null,
                totalDuration = 0f
            )

        // Initial state
        chartLines.forEach { line ->
            val initialValue: Float = if (line.getValue(firstPhase)) line.yAxisValueOn else line.yAxisValueOff
            valuesMap.getValue(line).add(Entry(0f, initialValue))
        }

        // Process transitions
        feedProfile.feedProfilePhases.forEach { phase ->
            val phaseDuration: Float = phase.duration.toFloat()
            if (phaseDuration <= 0f) return@forEach

            chartLines.forEach { line ->
                val entries: MutableList<Entry> = valuesMap.getValue(key = line)
                val lastValue: Float = entries.last().y
                val newValue: Float = if (line.getValue(phase)) line.yAxisValueOn else line.yAxisValueOff

                if (abs(x = newValue - lastValue) > 0.1f) { // chartValAllowedDelta
                    entries.add(Entry( cumulatedTime, lastValue))
                    entries.add(Entry( cumulatedTime, newValue))
                }
            }

            cumulatedTime += phaseDuration

            chartLines.forEach { line ->
                val entries = valuesMap.getValue(line)
                val newValue = if (line.getValue(phase)) line.yAxisValueOn else line.yAxisValueOff
                entries.add(Entry(cumulatedTime, newValue))
            }
        }

        val lineDataMap: Map<FeedProfileSignal, LineData> = chartLines.associate { line ->
            val set: LineDataSet = LineDataSet(valuesMap.getValue(line), line.name).apply {
                color = feedProfileLineColorDefinition(signal = line.signal)
                lineWidth = 2.5f
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawValues(false)
                setDrawCircles(false)
            }
            line.signal to LineData(set)
        }

        return FeedProfileChartDataBundle(
            lineDataFeeder = lineDataMap[FeedProfileSignal.FEEDER],
            lineDataSkimmer = lineDataMap[FeedProfileSignal.SKIMMER],
            lineDataMainPump1 = lineDataMap[FeedProfileSignal.MAIN_PUMP1],
            lineDataMainPump2 = lineDataMap[FeedProfileSignal.MAIN_PUMP2],
            lineDataAuxPump1 = lineDataMap[FeedProfileSignal.AUX_PUMP1],
            lineDataAuxPump2 = lineDataMap[FeedProfileSignal.AUX_PUMP2],
            totalDuration = cumulatedTime
        )
    }
}
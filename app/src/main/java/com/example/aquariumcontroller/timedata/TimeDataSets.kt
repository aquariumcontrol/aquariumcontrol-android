package com.example.aquariumcontroller.timedata

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color
import com.github.mikephil.charting.components.YAxis
import java.sql.Timestamp

class TimeDataSets(timeData: List<TimeDataSample>) {
    val dataTemperature: LineData
    val datapH: LineData
    val dataConductivity: LineData
    val dataTankLevel: LineData
    val dataAmbient: LineData
    val firstTimestamp: Timestamp
    val lastTimestamp: Timestamp
    val length: Int = timeData.size

    init {
        val valuesTemperature = ArrayList<Entry>()
        val valuesTemperatureFiltered = ArrayList<Entry>()
        val valuespH = ArrayList<Entry>()
        val valuespHFiltered = ArrayList<Entry>()
        val valuesConductivity = ArrayList<Entry>()
        val valuesConductivityFiltered = ArrayList<Entry>()
        val valuesMainTankLevelSwitch = ArrayList<Entry>()
        val valuesRefillInProgress = ArrayList<Entry>()
        val valuesAmbientTemperature = ArrayList<Entry>()
        val valuesAmbientHumidity = ArrayList<Entry>()
        val valuesSurfaceVentilation = ArrayList<Entry>()
        val valuesHeating = ArrayList<Entry>()

        val itTimeData = timeData.iterator()

        if (timeData.isNotEmpty()) {
            firstTimestamp = timeData[0].timestamp
            lastTimestamp = timeData[timeData.size - 1].timestamp
        }
        else {
            firstTimestamp = Timestamp(0)
            lastTimestamp = Timestamp(0)
        }

        while (itTimeData.hasNext()) {
            val timeDataSample = itTimeData.next()

            valuesTemperature.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.temperature.toFloat()
                )
            )
            valuesTemperatureFiltered.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.temperatureFiltered.toFloat()
                )
            )
            valuespH.add(Entry(timeDataSample.timeSeconds().toFloat(), timeDataSample.pH.toFloat()))
            valuespHFiltered.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.pHFiltered.toFloat()
                )
            )
            valuesConductivity.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.conductivity.toFloat()
                )
            )
            valuesConductivityFiltered.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.conductivityFiltered.toFloat()
                )
            )
            valuesMainTankLevelSwitch.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.mainTankLevelSwitchStatus.toFloat()
                )
            )
            valuesRefillInProgress.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.refillInProgress.toFloat()
                )
            )
            valuesAmbientTemperature.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.ambientTemperature.toFloat()
                )
            )
            valuesAmbientHumidity.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.ambientHumidity.toFloat()
                )
            )
            valuesSurfaceVentilation.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.surfaceVentilation.toFloat()
                )
            )
            valuesHeating.add(
                Entry(
                    timeDataSample.timeSeconds().toFloat(),
                    timeDataSample.heater.toFloat()
                )
            )
        }

        // create a dataset and give it a type
        val setTemperature = LineDataSet(valuesTemperature, "Temperature")
        val setTemperatureFiltered = LineDataSet(valuesTemperatureFiltered, "Temperature filtered")
        val setpH = LineDataSet(valuespH, "pH")
        val setpHFiltered = LineDataSet(valuespHFiltered, "pH filtered")
        val setConductivity = LineDataSet(valuesConductivity, "Conductivity")
        val setConductivityFiltered =
            LineDataSet(valuesConductivityFiltered, "Conductivity filtered")
        val setMainTankLevelSwitch =
            LineDataSet(valuesMainTankLevelSwitch, "Main tank level switch")
        val setRefillInProgress = LineDataSet(valuesRefillInProgress, "Refill in progress")
        val setAmbientTemperature = LineDataSet(valuesAmbientTemperature, "Ambient Temperature")
        val setAmbientHumidity = LineDataSet(valuesAmbientHumidity, "Ambient Humidity")
        val setSurfaceVentilation = LineDataSet(valuesSurfaceVentilation, "Surface Ventilation")
        val setHeating = LineDataSet(valuesHeating, "Heating")

        setTemperature.color = Color.RED
        setTemperatureFiltered.color = Color.BLUE
        setpH.color = Color.RED
        setpHFiltered.color = Color.BLUE
        setConductivity.color = Color.RED
        setConductivityFiltered.color = Color.BLUE
        setMainTankLevelSwitch.color = Color.YELLOW
        setRefillInProgress.color = Color.BLUE
        setAmbientTemperature.color = Color.BLUE
        setAmbientHumidity.color = Color.RED
        setSurfaceVentilation.color = Color.YELLOW
        setHeating.color = Color.GREEN

        setTemperature.lineWidth = 1.5f
        setTemperatureFiltered.lineWidth = 1.5f
        setpH.lineWidth = 1.5f
        setpHFiltered.lineWidth = 1.5f
        setConductivity.lineWidth = 1.5f
        setConductivityFiltered.lineWidth = 1.5f
        setMainTankLevelSwitch.lineWidth = 1.5f
        setRefillInProgress.lineWidth = 1.5f
        setAmbientTemperature.lineWidth = 1.5f
        setAmbientHumidity.lineWidth = 1.5f
        setSurfaceVentilation.lineWidth = 1.5f
        setHeating.lineWidth = 1.5f

        setTemperature.axisDependency = YAxis.AxisDependency.LEFT
        setTemperatureFiltered.axisDependency = YAxis.AxisDependency.LEFT
        setHeating.axisDependency = YAxis.AxisDependency.RIGHT
        setSurfaceVentilation.axisDependency = YAxis.AxisDependency.RIGHT
        setpH.axisDependency = YAxis.AxisDependency.LEFT
        setpHFiltered.axisDependency = YAxis.AxisDependency.LEFT
        setConductivity.axisDependency = YAxis.AxisDependency.LEFT
        setConductivityFiltered.axisDependency = YAxis.AxisDependency.LEFT
        setMainTankLevelSwitch.axisDependency = YAxis.AxisDependency.LEFT
        setRefillInProgress.axisDependency = YAxis.AxisDependency.LEFT
        setAmbientTemperature.axisDependency = YAxis.AxisDependency.LEFT
        setAmbientHumidity.axisDependency = YAxis.AxisDependency.RIGHT

        setTemperature.setDrawValues(false)
        setTemperatureFiltered.setDrawValues(false)
        setpH.setDrawValues(false)
        setpHFiltered.setDrawValues(false)
        setConductivity.setDrawValues(false)
        setConductivityFiltered.setDrawValues(false)
        setMainTankLevelSwitch.setDrawValues(false)
        setRefillInProgress.setDrawValues(false)
        setAmbientTemperature.setDrawValues(false)
        setAmbientHumidity.setDrawValues(false)
        setSurfaceVentilation.setDrawValues(false)
        setHeating.setDrawValues(false)

        setTemperature.setDrawCircles(false)
        setTemperatureFiltered.setDrawCircles(false)
        setpH.setDrawCircles(false)
        setpHFiltered.setDrawCircles(false)
        setConductivity.setDrawCircles(false)
        setConductivityFiltered.setDrawCircles(false)
        setMainTankLevelSwitch.setDrawCircles(false)
        setRefillInProgress.setDrawCircles(false)
        setAmbientTemperature.setDrawCircles(false)
        setAmbientHumidity.setDrawCircles(false)
        setSurfaceVentilation.setDrawCircles(false)
        setHeating.setDrawCircles(false)

        setTemperature.mode = LineDataSet.Mode.LINEAR
        setTemperatureFiltered.mode = LineDataSet.Mode.LINEAR
        setpH.mode = LineDataSet.Mode.LINEAR
        setpHFiltered.mode = LineDataSet.Mode.LINEAR
        setConductivity.mode = LineDataSet.Mode.LINEAR
        setConductivityFiltered.mode = LineDataSet.Mode.LINEAR
        setMainTankLevelSwitch.mode = LineDataSet.Mode.LINEAR
        setRefillInProgress.mode = LineDataSet.Mode.LINEAR
        setAmbientTemperature.mode = LineDataSet.Mode.LINEAR
        setAmbientHumidity.mode = LineDataSet.Mode.LINEAR
        setSurfaceVentilation.mode = LineDataSet.Mode.LINEAR
        setHeating.mode = LineDataSet.Mode.LINEAR

        setTemperature.setDrawFilled(false)
        setTemperatureFiltered.setDrawFilled(false)
        setpH.setDrawFilled(false)
        setpHFiltered.setDrawFilled(false)
        setConductivity.setDrawFilled(false)
        setConductivityFiltered.setDrawFilled(false)
        setMainTankLevelSwitch.setDrawFilled(false)
        setRefillInProgress.setDrawFilled(false)
        setAmbientTemperature.setDrawFilled(false)
        setAmbientHumidity.setDrawFilled(false)
        setSurfaceVentilation.setDrawFilled(false)
        setHeating.setDrawFilled(false)

        // create data objects with the data sets
        dataTemperature = LineData(setSurfaceVentilation)
        dataTemperature.addDataSet(setHeating)
        dataTemperature.addDataSet(setTemperature)
        dataTemperature.addDataSet(setTemperatureFiltered)

        datapH = LineData(setpH)
        datapH.addDataSet(setpHFiltered)

        dataConductivity = LineData(setConductivity)
        dataConductivity.addDataSet(setConductivityFiltered)

        dataTankLevel = LineData(setMainTankLevelSwitch)
        dataTankLevel.addDataSet(setRefillInProgress)

        dataAmbient = LineData(setAmbientTemperature)
        dataAmbient.addDataSet(setAmbientHumidity)

    }
}
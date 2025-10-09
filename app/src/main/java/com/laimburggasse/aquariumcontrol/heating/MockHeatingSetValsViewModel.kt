package com.laimburggasse.aquariumcontrol.heating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.laimburggasse.aquariumcontrol.MainViewModel

class MockHeatingSetValsViewModel(
    override var heatingSetValsUiState: HeatingSetValsUiState
) : HeatingSetValsViewModelInterface {

    override val sliderRange: LiveData<ClosedFloatingPointRange<Float>> =
        MutableLiveData(22.0f..28.0f)

    override val lowerBoundValue: LiveData<Double> = MutableLiveData(22.0)

    override val upperBoundValue: LiveData<Double> = MutableLiveData(28.0)

    override val onRangeChangedBySlider: (Float, Float) -> Unit = {
        start, end ->
        // do nothing
    }
    override val onLowerValueChanged: (Float) -> Unit = {
        value ->
        // do nothing
    }

    override val onUpperValueChanged: (Float) -> Unit = {
        value ->
        // do nothing
    }

    override fun loadData(mainViewModel: MainViewModel) {
        // do nothing
    }

    override fun updateValues(mainViewModel: MainViewModel) {
        // do nothing
    }

    override fun resetValues() {
        // do nothing
    }
}
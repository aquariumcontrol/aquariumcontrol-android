package com.laimburggasse.aquariumcontrol.feed.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class FeedProfileEditMenuItem(
    val id: Int,
    val level: Int,
    val text: String,
    val isToggleButton: Boolean = false,
    val phaseType: FeedProfileEditPhaseType? = null,
    val toggleButtonType: FeedProfileEditToggleButtonType? = null,

    val isChecked: MutableState<Boolean> = mutableStateOf(false),

    val isNumericInput: Boolean = false,

    val durationInitialValue: Double = 0.0,

    val children: List<FeedProfileEditMenuItem> = emptyList(),

    val isExpanded: MutableState<Boolean> = mutableStateOf(false)
) {
    private val _inputValue: MutableLiveData<Double> = MutableLiveData<Double>(durationInitialValue)
    val inputValue: LiveData<Double> = _inputValue

    fun updateIsChecked(newValue: Boolean) {
        this.isChecked.value = newValue
    }

    fun updateIsExpanded(newValue: Boolean) {
        this.isExpanded.value = newValue
    }

    fun updateInputValue(newValue: Double) {
        this._inputValue.value = newValue
    }
}

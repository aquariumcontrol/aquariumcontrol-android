package com.example.aquariumcontroller.feed.edit

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
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
        Log.d("FeedProfileEditMenuItem", "updating isChecked to $newValue")
        this.isChecked.value = newValue
    }

    fun updateIsExpanded(newValue: Boolean) {
        Log.d("FeedProfileEditMenuItem", "updating isExpanded to $newValue")
        this.isExpanded.value = newValue
    }

    fun updateInputValue(newValue: Double) {
        Log.d("FeedProfileEditMenuItem", "updating inputValue to $newValue")
        this._inputValue.value = newValue
    }
}

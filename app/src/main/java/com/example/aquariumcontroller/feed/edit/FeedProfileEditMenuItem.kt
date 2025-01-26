package com.example.aquariumcontroller.feed.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf

data class FeedProfileEditMenuItem(
    val id: Int,
    val level: Int,
    val text: String,
    val isToggleButton: Boolean = false,
    val toggleButtonType: FeedProfileEditToggleButtonType? = null,
    val isChecked: MutableState<Boolean> = mutableStateOf(false),
    val isNumericInput: Boolean = false,
    val inputValue: MutableState<Double> = mutableDoubleStateOf(0.0),
    val children: List<FeedProfileEditMenuItem> = emptyList(),
    var isExpanded: MutableState<Boolean> = mutableStateOf(false)
)

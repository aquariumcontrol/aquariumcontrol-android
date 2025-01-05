package com.example.aquariumcontroller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    //private val _message = MutableLiveData("Hi $userId")
    //val message: LiveData<String> = _message
    internal var controllerIsLoaded by mutableStateOf(false)
}
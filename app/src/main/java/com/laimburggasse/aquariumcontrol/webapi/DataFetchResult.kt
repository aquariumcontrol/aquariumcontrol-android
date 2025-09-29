package com.laimburggasse.aquariumcontrol.webapi

sealed class DataFetchResult<out T> {
    data class Success<out T>(val data: T) : DataFetchResult<T>()
    data class Error(val exception: Exception?, val errorMessage: String) : DataFetchResult<Nothing>()
}

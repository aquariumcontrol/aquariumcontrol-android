package com.laimburggasse.aquariumcontrol.common.data.remote

sealed class DataFetchResult<out T> {
    data class Success<out T>(val data: T) : DataFetchResult<T>()
    data class Error(val exception: Exception?, val errorMessage: String) : DataFetchResult<Nothing>()
    object Loading: DataFetchResult<Nothing>()
}

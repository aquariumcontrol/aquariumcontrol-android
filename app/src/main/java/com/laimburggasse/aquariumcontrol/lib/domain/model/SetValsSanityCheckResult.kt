package com.laimburggasse.aquariumcontrol.lib.domain.model

sealed interface SetValsSanityCheckResult<out T> {
    data class Success<T>(val data: T) : SetValsSanityCheckResult<T>
    data class Error(val errorMessage: String) : SetValsSanityCheckResult<Nothing>
}
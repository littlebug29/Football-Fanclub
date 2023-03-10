package com.khanhtq.football.common

sealed class UiState<out T> {
    data class Succeeded<out T>(val data: T) : UiState<T>()

    data class Failed(val errorMessage: String) : UiState<Nothing>()

    object Loading : UiState<Nothing>()
}

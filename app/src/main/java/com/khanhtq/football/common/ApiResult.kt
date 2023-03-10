package com.khanhtq.football.common

import java.io.IOException

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()

    data class Error(val errorType: ErrorType, val errorMessage: String) : ApiResult<Nothing>()
}

enum class ErrorType(val defaultMessage: String?) {
    NETWORK_ERROR("Please connect to a network!"),
    SERVER_ERROR(null)
}

fun Exception.toError(): ApiResult.Error {
    val errorType = when (this) {
        is IOException -> ErrorType.NETWORK_ERROR
        else -> ErrorType.SERVER_ERROR
    }
    val errorMessage = when (errorType) {
        ErrorType.NETWORK_ERROR -> errorType.defaultMessage.orEmpty()
        else -> this.message.orEmpty()
    }
    return ApiResult.Error(errorType, errorMessage)
}

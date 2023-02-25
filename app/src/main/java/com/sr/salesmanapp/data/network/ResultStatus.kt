package com.sr.salesmanapp.data.network

sealed class ResultStatus<out T> {
    data class Success<out R>(val data:R) : ResultStatus<R>()
    data class Failure(val t:Throwable): ResultStatus<Nothing>()
    object Loading : ResultStatus<Nothing>()
}

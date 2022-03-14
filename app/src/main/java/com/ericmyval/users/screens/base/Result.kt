package com.ericmyval.users.screens.base

typealias Mapper<Input, Output> = (Input) -> Output

sealed class Result<T> {
    fun <R> map(mapper: Mapper<T, R>? = null): Result<R> = when(this) {
        is EmptyResult -> EmptyResult()
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(this.error)
        is SuccessResult -> {
            if (mapper == null) throw IllegalArgumentException("Mapper should not be NULL for success result")
            SuccessResult(mapper(this.data))
        }
    }

}

class PendingResult<T> : Result<T>()

class EmptyResult<T> : Result<T>()

class SuccessResult<T>(
    val data: T
) : Result<T>()

class ErrorResult<T>(
    val error: Throwable
) : Result<T>()

fun <T> Result<T>?.takeSuccess(): T? {
    return if (this is SuccessResult)
        this.data
    else
        null
}
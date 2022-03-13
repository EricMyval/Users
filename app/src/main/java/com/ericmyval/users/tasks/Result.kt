package com.ericmyval.users.tasks

sealed class Result<T> {
    @Suppress("UNCHECKED_CAST")
    fun <R> map(mapper: (T) -> R): Result<R> {
        if (this is SuccessResult)
            return SuccessResult(mapper(data))
        return this as Result<R>
    }

/*  @JvmName("map1") typealias Mapper<Input, Output> = (Input) -> Output
    fun <R> map(mapper: Mapper<T, R>? = null): Result<R> = when(this) {
        is EmptyResult -> EmptyResult()
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(this.error)
        is SuccessResult -> {
            if (mapper == null) throw IllegalArgumentException("Mapper should not be NULL for success result")
            SuccessResult(mapper(this.data))
        }
    }
     */
}

class SuccessResult<T>(
    val data: T
) : Result<T>()

class ErrorResult<T>(
    val error: Throwable
) : Result<T>()

class PendingResult<T> : Result<T>()

class EmptyResult<T> : Result<T>()
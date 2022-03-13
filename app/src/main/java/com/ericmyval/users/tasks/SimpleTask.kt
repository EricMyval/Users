package com.ericmyval.users.tasks

import java.util.concurrent.Callable

class SimpleTask<T>(
    callable: Callable<T>
) : Task<T> {

    private var result: Result<T> = PendingResult()

    init {
        result = try {
            SuccessResult(callable.call())
        } catch (e: Throwable) {
            ErrorResult(e)
        }
        notifyListeners()
    }

    private var valueCallback: Callback<T>? = null
    private var errorCallback: Callback<Throwable>? = null

    override fun onSuccess(callback: Callback<T>): Task<T> {
        this.valueCallback = callback
        notifyListeners()
        return this
    }

    override fun onError(callback: Callback<Throwable>): Task<T> {
        this.errorCallback = callback
        notifyListeners()
        return this
    }

    override fun cancel() {
        clear()
    }

    private fun notifyListeners() {
        val result = this.result
        val callback = this.valueCallback
        val errorCallback = this.errorCallback
        if (result is SuccessResult && callback != null) {
            callback(result.data)
            clear()
        } else if (result is ErrorResult && errorCallback != null) {
            errorCallback.invoke(result.error)
            clear()
        }
    }

    private fun clear() {
        valueCallback = null
        errorCallback = null
    }
}


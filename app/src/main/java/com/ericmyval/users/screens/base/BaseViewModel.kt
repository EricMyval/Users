package com.ericmyval.users.screens.base

import androidx.lifecycle.*
import kotlinx.coroutines.*

//typealias MutableLiveResult<T> = MutableLiveData<Result<T>>

open class BaseViewModel : ViewModel() {
    private val _actionShowToast = MutableLiveData<Event<Int>>()
    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    private val _actionGoNavigate = MutableLiveData<Event<ItemNavigate>>()

    val actionShowToast: LiveData<Event<Int>> = _actionShowToast
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack
    val actionGoNavigate: LiveData<Event<ItemNavigate>> = _actionGoNavigate

    private val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, _ -> }
    protected val viewModelScope = CoroutineScope(coroutineContext)

    override fun onCleared() {
        super.onCleared()
        clearScope()
    }

    fun goBack() {
        _actionGoBack.value = Event(Unit)
    }

    fun goShowToast(res: Int) {
        viewModelScope.launch { Dispatchers.Main
            _actionShowToast.value = Event(res)
        }
    }

    fun goNavigate(args: ItemNavigate) {
        _actionGoNavigate.value = Event(args)
    }

    /*
    fun <T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                liveResult.postValue(SuccessResult(block()))
            } catch (e: Throwable) {
                if (e !is CancellationException)
                    liveResult.postValue(ErrorResult(e))
            }
        }
    }
     */

    private fun clearScope() {
        viewModelScope.cancel()
    }

    open fun onResumeView() {}

}
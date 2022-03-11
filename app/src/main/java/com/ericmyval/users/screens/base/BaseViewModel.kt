package com.ericmyval.users.screens.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ericmyval.users.tasks.Task

open class BaseViewModel : ViewModel() {
    private val tasks = mutableListOf<Task<*>>()

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    private val _actionGoNavigate = MutableLiveData<Event<ItemNavigate>>()

    val actionShowToast: LiveData<Event<Int>> = _actionShowToast
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack
    val actionGoNavigate: LiveData<Event<ItemNavigate>> = _actionGoNavigate

    override fun onCleared() {
        super.onCleared()
        tasks.forEach { it.cancel() }
    }

    fun <T> Task<T>.autoCancel() {
        tasks.add(this)
    }

    fun goBack() {
        _actionGoBack.value = Event(Unit)
    }

    fun goShowToast(res: Int) {
        _actionShowToast.value = Event(res)
    }

    fun goNavigate(args: ItemNavigate) {
        _actionGoNavigate.value = Event(args)
    }
}
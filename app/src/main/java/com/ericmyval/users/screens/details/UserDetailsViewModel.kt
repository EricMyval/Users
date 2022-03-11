package com.ericmyval.users.screens.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.ericmyval.users.R
import com.ericmyval.users.model.UserDetails
import com.ericmyval.users.model.UsersService
import com.ericmyval.users.screens.base.BaseViewModel
import com.ericmyval.users.screens.base.Event
import com.ericmyval.users.tasks.EmptyResult
import com.ericmyval.users.tasks.PendingResult
import com.ericmyval.users.tasks.SuccessResult
import com.ericmyval.users.tasks.Result

class UserDetailsViewModel(
    private val usersService: UsersService
): BaseViewModel() {

    // для работы внутри и подписка для нашего фрагмента
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    // Экшены
    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast
    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack

    private val currentState: State get() = state.value!!

    init {
        _state.value = State(
            userDetailsResult = EmptyResult(),
            deletingInProgress = false
        )
    }

    fun loadUser(userId: Long) {
        if (currentState.userDetailsResult !is EmptyResult)
            return

        _state.value = currentState.copy(userDetailsResult = PendingResult())

        usersService.getById(userId)
            .onSuccess {
                _state.value = currentState.copy(userDetailsResult = SuccessResult(it))
            }
            .onError {
                // todo
                //findNavController().popBackStack()
                _actionShowToast.value = Event(R.string.cant_load_user_details)
                _actionGoBack.value = Event(Unit)
            }
            .autoCancel()
    }

    fun deleteUser() {
        val userDetailsResult = currentState.userDetailsResult
        if (userDetailsResult !is SuccessResult)
            return
        _state.value = currentState.copy(deletingInProgress = true)
        usersService.deleteUser(userDetailsResult.data.user)
            .onSuccess {
                _actionShowToast.value = Event(R.string.user_has_been_deleted)
                _actionGoBack.value = Event(Unit)
            }
            .onError {
                _state.value = currentState.copy(deletingInProgress = false)
                _actionShowToast.value = Event(R.string.cant_delete_user)
            }
            .autoCancel()
    }


    data class State(
        val userDetailsResult: Result<UserDetails>,
        private val deletingInProgress: Boolean
    ) {
        val showContent: Boolean get() = userDetailsResult is SuccessResult
        val showProgress: Boolean get() = userDetailsResult is PendingResult || deletingInProgress
        val enableDeleteButton: Boolean get() = !deletingInProgress
    }
}
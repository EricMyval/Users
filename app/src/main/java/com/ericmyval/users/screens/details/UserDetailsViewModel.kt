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
                goShowToast(R.string.cant_load_user_details)
                goBack()
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
                goShowToast(R.string.user_has_been_deleted)
                goBack()
            }
            .onError {
                _state.value = currentState.copy(deletingInProgress = false)
                goShowToast(R.string.cant_delete_user)
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
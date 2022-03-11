package com.ericmyval.users.screens.users

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ericmyval.users.R
import com.ericmyval.users.model.User
import com.ericmyval.users.model.UsersListener
import com.ericmyval.users.model.UsersService
import com.ericmyval.users.screens.base.BaseViewModel
import com.ericmyval.users.screens.base.ItemNavigate
import com.ericmyval.users.screens.details.UserDetailsFragment
import com.ericmyval.users.tasks.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsersListViewModel(
    private val usersService: UsersService
): BaseViewModel(), UserActionListener {

    // для работы внутри и подписка для нашего фрагмента
    private val _users = MutableLiveData<Result<List<UserListItem>>>()
    val users: LiveData<Result<List<UserListItem>>> = _users

    private val userIdsInProgress = mutableSetOf<Long>()
    private var usersResult: Result<List<User>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: UsersListener = {
        usersResult = if (it.isEmpty()) {
            EmptyResult()
        } else
            SuccessResult(it)
    }

    init {
        usersService.addListener(listener)
        loadUsers()

        viewModelScope.launch {
            delay(1000)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Очистка для избежания утечек
        usersService.removeListener(listener)
    }

    // Работа со списком
    override fun onUserMove(user: User, moveBy: Int) {
        if (isInProgress(user))
            return
        addProgressTo(user)
        usersService.moveUser(user, moveBy)
            .onSuccess {
                removeProgressFrom(user)
                goShowToast(R.string.user_has_been_moved)
            }
            .onError {
                removeProgressFrom(user)
                goShowToast(R.string.cant_move_user)
            }
            .autoCancel()
    }

    override fun onUserDelete(user: User) {
        if (isInProgress(user))
            return
        addProgressTo(user)
        usersService.deleteUser(user)
            .onSuccess {
                removeProgressFrom(user)
                goShowToast(R.string.user_has_been_deleted)
            }
            .onError {
                removeProgressFrom(user)
                goShowToast(R.string.cant_delete_user)
            }
            .autoCancel()
    }
    override fun onUserDetails(user: User) {
        goNavigate(ItemNavigate(
            R.id.action_usersListFragment_to_userDetailsFragment,
            bundleOf(UserDetailsFragment.USER_ID to user.id)
        ))
    }

    override fun onUserFire(user: User) {
        usersService.fireUser(user)
    }

    fun loadUsers() {
        usersResult = PendingResult()
        usersService.loadUsers()
            .onError {
                usersResult = ErrorResult(it)
            }
            .autoCancel()
    }

    // Прогресс
    private fun addProgressTo(user: User) {
        userIdsInProgress.add(user.id)
        notifyUpdates()
    }
    private fun removeProgressFrom(user: User) {
        userIdsInProgress.remove(user.id)
        notifyUpdates()
    }
    private fun isInProgress(user: User): Boolean {
        return userIdsInProgress.contains(user.id)
    }

    private fun notifyUpdates() {
        // postValue - синхра в основной поток
        _users.postValue(usersResult.map { users ->
            users.map { user -> UserListItem(user, isInProgress(user)) }
        })
    }
}
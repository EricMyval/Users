package com.ericmyval.users.screens.users

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ericmyval.users.R
import com.ericmyval.users.model.User
import com.ericmyval.users.model.UsersListener
import com.ericmyval.users.model.UsersService
import com.ericmyval.users.screens.base.BaseViewModel
import com.ericmyval.users.screens.base.ItemNavigate
import com.ericmyval.users.screens.details.UserDetailsFragment
import com.ericmyval.users.tasks.*
import kotlinx.coroutines.*

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
        usersResult = if (it.isEmpty())
            EmptyResult()
        else
            SuccessResult(it)
    }



    init {
        usersService.addListener(listener)
        loadUsers()
    }
    override fun onCleared() {
        super.onCleared()
        usersService.removeListener(listener)
    }




    override fun onUserMove(user: User, moveBy: Int) {
        if (!isInProgress(user))
            viewModelScope.launch {
                try {
                    addProgressTo(user)
                    usersResult = SuccessResult(usersService.moveUser(user, moveBy))
                    removeProgressFrom(user)
                    goShowToast(R.string.user_has_been_moved)
                } catch (e: Throwable) {
                    removeProgressFrom(user)
                    usersResult = ErrorResult(e)
                    goShowToast(R.string.cant_move_user)
                }
            }
    }
    override fun onUserDelete(user: User) {
        if (!isInProgress(user))
            viewModelScope.launch {
                try {
                    addProgressTo(user)
                    usersService.deleteUser(user)
                    removeProgressFrom(user)
                    goShowToast(R.string.user_has_been_deleted)
                } catch (e: Throwable) {
                    removeProgressFrom(user)
                    goShowToast(R.string.cant_delete_user)
                }
            }
    }
    override fun onUserFire(user: User) {
        viewModelScope.launch {
            try {
                usersResult = SuccessResult(usersService.fireUser(user))
            } catch (e: Throwable) {
                goShowToast(R.string.cant_fire_user)
            }
        }
    }
    private fun loadUsers() {
        viewModelScope.launch {
            try {
                usersResult = SuccessResult(usersService.loadUsers())
            } catch (e: Throwable) {
                goShowToast(R.string.cant_load_users)
            }
        }
    }






    override fun onUserDetails(user: User) {
        goNavigate(ItemNavigate(
            R.id.action_usersListFragment_to_userDetailsFragment,
            bundleOf(UserDetailsFragment.USER_ID to user.id)
        ))
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
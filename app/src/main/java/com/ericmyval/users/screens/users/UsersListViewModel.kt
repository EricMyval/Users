package com.ericmyval.users.screens.users

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ericmyval.users.R
import com.ericmyval.users.model.User
import com.ericmyval.users.model.UsersService
import com.ericmyval.users.screens.base.*
import com.ericmyval.users.screens.details.UserDetailsFragment
import kotlinx.coroutines.*

class UsersListViewModel(
    private val usersService: UsersService
): BaseViewModel(), UserActionListener {

    private val _users = MutableLiveData<Result<List<UserListItem>>>()
    val users: LiveData<Result<List<UserListItem>>> = _users
    private val userIdsInProgress = mutableSetOf<Long>()
    private var usersResult: Result<List<User>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    init {
        loadUsers()
    }

    override fun onUserMove(user: User, moveBy: Int) = viewModelScope.launch {
        try {
            addProgressTo(user)
            usersResult = SuccessResult(usersService.moveUser(user, moveBy))
            launch { Dispatchers.Main
                goShowToast(R.string.user_has_been_moved)
            }
        } catch (e: Throwable) {
            launch { Dispatchers.Main
                goShowToast(R.string.cant_move_user)
            }
        } finally {
            removeProgressFrom(user)
        }
    }
    override fun onUserDelete(user: User) = viewModelScope.launch {
        try {
            addProgressTo(user)
            usersResult = SuccessResult(usersService.deleteUser(user))
            launch { Dispatchers.Main
                goShowToast(R.string.user_has_been_deleted)
            }
        } catch (e: Throwable) {
            launch { Dispatchers.Main
                goShowToast(R.string.cant_delete_user)
            }
        } finally {
            removeProgressFrom(user)
        }
    }
    override fun onUserFire(user: User) = viewModelScope.launch {
        try {
            addProgressTo(user)
            usersResult = SuccessResult(usersService.fireUser(user))
            launch { Dispatchers.Main
                goShowToast(R.string.user_has_been_fire)
            }
        } catch (e: Throwable) {
            goShowToast(R.string.cant_fire_user)
        } finally {
            removeProgressFrom(user)
        }
    }
    //private fun load() = into(_usersResult) { usersService.loadUsers() }
    private fun loadUsers() = viewModelScope.launch {
        try {
            usersResult = SuccessResult(usersService.loadUsers())
        } catch (e: Throwable) {
            launch { Dispatchers.Main
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
        _users.postValue(usersResult.map { users ->
            users.map { user -> UserListItem(user, isInProgress(user)) }
        })
    }

}
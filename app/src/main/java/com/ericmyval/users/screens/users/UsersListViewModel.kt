package com.ericmyval.users.screens.users

import android.view.View
import androidx.core.os.bundleOf
import com.ericmyval.users.R
import com.ericmyval.users.model.*
import com.ericmyval.users.screens.base.*
import com.ericmyval.users.screens.details.UserDetailsFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class UsersListViewModel(
    private val usersService: UsersService
): BaseViewModel(), UserActionListener {

    private val _users = MutableStateFlow<Result<List<UserListItem>>>(PendingResult())
    private val _loadProgress = MutableStateFlow<Progress>(EmptyProgress)

    val viewState: Flow<Result<ViewState>> = combine(
        _users,
        _loadProgress,
        ::mergeSources
    )

    data class ViewState(
        val usersList: List<UserListItem>,
        val showProgress: Boolean,
        val saveProgressPercentage: Int,
    )

    private fun mergeSources(usersList: Result<List<UserListItem>>, loadProgress: Progress): Result<ViewState> {
        return usersList.map {
            ViewState(
                usersList = it,
                showProgress = loadProgress.isInProgress(),
                saveProgressPercentage = loadProgress.getPercentage()
            )
        }
    }

    private val userIdsInProgress = mutableSetOf<Long>()
    private var usersResult: Result<List<User>> = PendingResult()

    init {
        viewModelScope.launch {
            usersService.listenUsers().collect {
                usersResult = it
                notifyUpdates()
            }
        }
        loadUsers()
    }

    override fun onUserMove(user: User, moveBy: Int) {
        onUserProgressAction(user, R.string.user_has_been_moved, R.string.cant_move_user) {
            usersService.moveUser(user, moveBy)
        }
    }
    override fun onUserDelete(user: User) {
        onUserProgressAction(user, R.string.user_has_been_deleted, R.string.cant_delete_user) {
            usersService.deleteUser(user)
        }
    }
    override fun onUserFire(user: User) {
        onUserProgressAction(user, R.string.user_has_been_fire, R.string.cant_fire_user) {
            usersService.fireUser(user)
        }
    }
    private fun loadUsers() = into(-1, R.string.cant_load_users) { usersService.loadUsers().collect() }
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
    private fun <T> onUserProgressAction(user: User, idMesSuccess: Int, idMesError: Int, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                addProgressTo(user)
                block()
                if (idMesSuccess != -1)
                    goShowToast(idMesSuccess)
            } catch (e: Exception) {
                if (e !is CancellationException && idMesError != -1)
                    goShowToast(idMesError)
            } finally {
                removeProgressFrom(user)
            }
        }
    }

    private fun notifyUpdates() {
        _users.value = usersResult.map { users -> users.map { user -> UserListItem(user, isInProgress(user)) } }
    }
}
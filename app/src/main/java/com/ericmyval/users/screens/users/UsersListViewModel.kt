package com.ericmyval.users.screens.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ericmyval.users.model.User
import com.ericmyval.users.model.UsersListener
import com.ericmyval.users.model.UsersService

class UsersListViewModel(
    private val usersService: UsersService
): ViewModel() {

    // для работы внутри
    private val _users = MutableLiveData<List<User>>()
    // подписка для нашего фрагмента
    val users: LiveData<List<User>> = _users

    private val listener: UsersListener = { _users.value = it}

    init {
        loadUsers()
    }

    override fun onCleared() {
        super.onCleared()
        // Очистка для избежания утечек
        usersService.removeListener(listener)
    }

    fun loadUsers() {
        usersService.addListener(listener)
    }

    fun moveUser(user: User, moveBy: Int) {
        usersService.moveUser(user, moveBy)
    }

    fun deleteUser(user: User) {
        usersService.deleteUser(user)
    }



}
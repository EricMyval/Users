package com.ericmyval.users.screens.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ericmyval.users.UserNotFoundException
import com.ericmyval.users.model.User
import com.ericmyval.users.model.UserDetails
import com.ericmyval.users.model.UsersService

class UserDetailsViewModel(
    private val usersService: UsersService
): ViewModel() {

    // для работы внутри
    private val _usersDetails = MutableLiveData<UserDetails>()
    // подписка для нашего фрагмента
    val usersDetails: LiveData<UserDetails> = _usersDetails

    fun loadUser(userId: Long) {
        if (_usersDetails.value != null)
            return
        try {
            _usersDetails.value = usersService.getById(userId)
        } catch (e: UserNotFoundException) {
            e.printStackTrace()
        }
    }

    fun deleteUser() {
        val userDetail = usersDetails.value ?: return
        usersService.deleteUser(userDetail.user)
    }
}
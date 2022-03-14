package com.ericmyval.users.screens.users

import com.ericmyval.users.model.User
import kotlinx.coroutines.Job

interface UserActionListener {
    fun onUserMove(user: User, moveBy: Int): Job
    fun onUserDelete(user: User): Job
    fun onUserDetails(user: User)
    fun onUserFire(user: User): Job
}
package com.ericmyval.users.screens.users

import com.ericmyval.users.model.User

// Лучше юзать интерфейс, т.к. дайствий будет несколько
interface UserActionListener {
    fun onUserMove(user: User, moveBy: Int)
    fun onUserDelete(user: User)
    fun onUserDetails(user: User)
    fun onUserFire(user: User)
}
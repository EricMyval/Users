package com.ericmyval.users.screens.users

import com.ericmyval.users.model.User

data class UserListItem(
    val user: User,
    val isInProgress: Boolean
)
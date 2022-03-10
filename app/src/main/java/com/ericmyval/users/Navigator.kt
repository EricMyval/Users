package com.ericmyval.users

import com.ericmyval.users.model.User

interface Navigator {
    fun showDetails(user: User)
    fun goBack()
    fun toast(messageRes: Int)
}
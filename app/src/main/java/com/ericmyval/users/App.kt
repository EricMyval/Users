package com.ericmyval.users

import android.app.Application
import com.ericmyval.users.model.UsersService

class App : Application() {

    val usersService = UsersService()
}
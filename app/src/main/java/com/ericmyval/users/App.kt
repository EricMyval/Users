package com.ericmyval.users

import android.app.Application
import com.ericmyval.users.model.UsersService
import com.ericmyval.users.model.coroutines.IODispatcher
import com.ericmyval.users.model.coroutines.WorkerDispatcher
import kotlinx.coroutines.Dispatchers

class App : Application() {

    private val ioDispatcher: IODispatcher = IODispatcher(Dispatchers.IO)
    private val workerDispatcher: WorkerDispatcher = WorkerDispatcher(Dispatchers.Default)

    val usersService = UsersService(ioDispatcher)
}
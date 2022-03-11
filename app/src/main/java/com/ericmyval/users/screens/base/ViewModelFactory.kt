package com.ericmyval.users.screens.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ericmyval.users.App
import com.ericmyval.users.ModalNotFoundException
import com.ericmyval.users.screens.details.UserDetailsViewModel
import com.ericmyval.users.screens.users.UsersListViewModel

class ViewModelFactory(
    private val app: App
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            UsersListViewModel::class.java -> UsersListViewModel(app.usersService)
            UserDetailsViewModel::class.java -> UserDetailsViewModel(app.usersService)
            else -> throw ModalNotFoundException()
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)

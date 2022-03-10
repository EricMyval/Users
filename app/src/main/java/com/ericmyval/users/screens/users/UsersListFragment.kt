package com.ericmyval.users.screens.users

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericmyval.users.R
import com.ericmyval.users.UserActionListener
import com.ericmyval.users.UsersAdapter
import com.ericmyval.users.databinding.FragmentUsersListBinding
import com.ericmyval.users.model.User
import com.ericmyval.users.screens.base.factory
import com.ericmyval.users.screens.details.UserDetailsFragment

class UsersListFragment: Fragment(R.layout.fragment_users_list) {

    private lateinit var binding: FragmentUsersListBinding
    private lateinit var adapter: UsersAdapter

    private val viewModel: UsersListViewModel by viewModels { factory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersListBinding.bind(view)

        adapter = UsersAdapter(object: UserActionListener {
            override fun onUserMove(user: User, moveBy: Int) {
                viewModel.moveUser(user, moveBy)
            }

            override fun onUserDelete(user: User) {
                viewModel.deleteUser(user)
            }

            override fun onUserDetails(user: User) {
                // navigator().showDetails(user)
                findNavController().navigate(
                    R.id.action_usersListFragment_to_userDetailsFragment, // nav action to be executed
                    bundleOf(UserDetailsFragment.USER_ID to user.id) // arguments for the destination
                )
            }
        })

        viewModel.users.observe(viewLifecycleOwner, Observer {
            adapter.users = it
        })

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

}
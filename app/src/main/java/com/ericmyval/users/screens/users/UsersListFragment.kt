package com.ericmyval.users.screens.users

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericmyval.users.R
import com.ericmyval.users.databinding.FragmentUsersListBinding
import com.ericmyval.users.screens.base.BaseFragment
import com.ericmyval.users.screens.base.factory
import com.ericmyval.users.screens.details.UserDetailsFragment

import com.ericmyval.users.tasks.EmptyResult
import com.ericmyval.users.tasks.ErrorResult
import com.ericmyval.users.tasks.PendingResult
import com.ericmyval.users.tasks.SuccessResult

class UsersListFragment: BaseFragment(R.layout.fragment_users_list) {
    override val viewModel: UsersListViewModel by viewModels { factory() }
    private lateinit var binding: FragmentUsersListBinding
    private lateinit var adapter: UsersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersListBinding.bind(view)
        adapter = UsersAdapter(viewModel)

        viewModel.users.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.users = it.data
                }
                is ErrorResult -> {
                    binding.tryAgainContainer.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.noUsersTextView.visibility = View.VISIBLE
                }
            }
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    private fun hideAll() {
        binding.recyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.tryAgainContainer.visibility = View.GONE
        binding.noUsersTextView.visibility = View.GONE
    }

}
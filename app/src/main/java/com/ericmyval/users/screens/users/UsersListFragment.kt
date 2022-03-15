package com.ericmyval.users.screens.users

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericmyval.users.R
import com.ericmyval.users.databinding.FragmentUsersListBinding
import com.ericmyval.users.screens.base.*

class UsersListFragment: BaseFragment(R.layout.fragment_users_list) {
    override val viewModel: UsersListViewModel by viewModels { factory() }
    private lateinit var binding: FragmentUsersListBinding
    private lateinit var adapter: UsersAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersListBinding.bind(view)
        adapter = UsersAdapter(viewModel)

        collectFlow(viewModel.users) {
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
                    binding.loadProgress.visibility = View.VISIBLE
                    binding.progressBar.progress = it.percent
                    binding.progressBarText.text = resources.getString(R.string.percentage_value, it.percent)
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
        binding.loadProgress.visibility = View.GONE
        binding.tryAgainContainer.visibility = View.GONE
        binding.noUsersTextView.visibility = View.GONE
    }

}
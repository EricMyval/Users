package com.ericmyval.users.screens.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ericmyval.users.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseFragment(
    idRes: Int
) : Fragment(idRes) {
    internal open val viewModel: BaseViewModel by viewModels { factory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeActionsLoad()
    }

    private fun observeActionsLoad() {
        viewModel.actionGoNavigate.observe(viewLifecycleOwner) {
            it.getValue()?.let { ItemNavigate ->
                findNavController().navigate(
                    R.id.action_usersListFragment_to_userDetailsFragment,
                    ItemNavigate.bundle)
            }
        }
        viewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes ->
                Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.actionGoBack.observe(viewLifecycleOwner) {
            it.getValue()?.let {
                findNavController().popBackStack()
            }
        }
    }
}

fun <T> BaseFragment.collectFlow(flow: Flow<T>, onCollect: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                onCollect(it)
            }
        }
    }
}
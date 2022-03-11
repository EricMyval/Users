package com.ericmyval.users.screens.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ericmyval.users.R
import com.ericmyval.users.databinding.FragmentUserDetailsBinding
import com.ericmyval.users.screens.base.factory
import com.ericmyval.users.tasks.SuccessResult

class UserDetailsFragment: Fragment(R.layout.fragment_user_details) {

    private lateinit var binding: FragmentUserDetailsBinding
    private val viewModel: UserDetailsViewModel by viewModels { factory() }

    companion object {
        const val USER_ID = "USER_ID"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserDetailsBinding.bind(view)

        viewModel.loadUser(requireArguments().getLong(USER_ID))

        viewModel.actionShowToast.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { messageRes ->
                Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.actionGoBack.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let {
                findNavController().popBackStack()
            }
        })

        viewModel.state.observe(viewLifecycleOwner, Observer {
            binding.contentContainer.visibility = if (it.showContent) {
                val userDetails = (it.userDetailsResult as SuccessResult).data
                binding.userNameTextView.text = userDetails.user.name
                Glide.with(this)
                    .load(userDetails.user.photo.ifBlank { R.drawable.ic_user_avatar })
                    .circleCrop()
                    .into(binding.photoImageView)
                binding.userDetailsTextView.text = userDetails.details
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.GONE
            binding.deleteButton.isEnabled = it.enableDeleteButton
        })

        binding.deleteButton.setOnClickListener {
            viewModel.deleteUser()
        }
    }
}
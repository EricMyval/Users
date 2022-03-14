package com.ericmyval.users.screens.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ericmyval.users.R
import com.ericmyval.users.databinding.FragmentUserDetailsBinding
import com.ericmyval.users.screens.base.BaseFragment
import com.ericmyval.users.screens.base.factory
import com.ericmyval.users.screens.base.SuccessResult

class UserDetailsFragment: BaseFragment(R.layout.fragment_user_details) {
    override val viewModel: UserDetailsViewModel by viewModels { factory() }
    private lateinit var binding: FragmentUserDetailsBinding

    companion object {
        const val USER_ID = "USER_ID"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserDetailsBinding.bind(view)

        viewModel.loadUser(requireArguments().getLong(USER_ID))

        viewModel.state.observe(viewLifecycleOwner) {
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
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteUser()
        }
    }
}
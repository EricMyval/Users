package com.ericmyval.users.screens.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ericmyval.users.R
import com.ericmyval.users.databinding.FragmentUserDetailsBinding
import com.ericmyval.users.screens.base.factory
import com.ericmyval.users.screens.base.navigator

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

        viewModel.usersDetails.observe(viewLifecycleOwner, Observer {
            binding.userNameTextView.text = it.user.name
            if (it.user.photo.isNotBlank()) {
                Glide.with(this)
                    .load(it.user.photo.ifBlank { R.drawable.ic_user_avatar })
                    .into(binding.photoImageView)
            }
            binding.userDetailsTextView.text = it.details
        })

        binding.deleteButton.setOnClickListener {
            viewModel.deleteUser()
            navigator().toast(R.string.user_has_been_deleted)
            navigator().goBack()
        }
    }
}
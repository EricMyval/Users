package com.ericmyval.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ericmyval.users.screens.details.UserDetailsFragment

// 1ч - основная логика

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun nav(fragment: Fragment) {
        fragment.findNavController().navigate(
            R.id.action_usersListFragment_to_userDetailsFragment,
            bundleOf(UserDetailsFragment.USER_ID to 10)
        )
    }


    /*
        override fun showDetails(user: User) {
        runWhenActive {
            supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, UserDetailsFragment.newInstance(user.id))
                    .commit()
        }
    }

    override fun goBack() {
        runWhenActive { onBackPressed() }
    }

    override fun toast(messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }
     */
}
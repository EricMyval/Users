package com.ericmyval.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ericmyval.users.databinding.ActivityMainBinding
import com.ericmyval.users.screens.users.UsersListFragment

// 1ч - основная логика

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, UsersListFragment())
                .commit()
    }
}
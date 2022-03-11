package com.ericmyval.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// 1ч  - основная логика
// 2ч  - переход к mvvm
// 30м - анимации через diffUtils
// 1ч  - Написание интерфейсов, оптимизация кода

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
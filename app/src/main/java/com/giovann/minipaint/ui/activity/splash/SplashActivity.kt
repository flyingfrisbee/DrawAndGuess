package com.giovann.minipaint.ui.activity.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.giovann.minipaint.R
import com.giovann.minipaint.ui.activity.menu.MenuActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1500L)
            startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
            finish()
        }
    }
}
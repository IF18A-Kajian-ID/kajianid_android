package com.kajianid.android.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.kajianid.android.R
import com.kajianid.android.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.version?.text = "Version ${getString(R.string.app_version)}"

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finishAffinity()
        }, 2000)
    }
}
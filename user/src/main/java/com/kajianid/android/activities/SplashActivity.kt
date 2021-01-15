package com.kajianid.android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.kajianid.android.R
import com.kajianid.android.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    //binding: pengganti findViewById untuk mencegah NullPointerException
    private var binding: ActivitySplashBinding? = null

    //pertama kali dijalankan
    //override: mengambil fungsi dari AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //mengatur tata letak komponen
        binding = ActivitySplashBinding.inflate(layoutInflater)

        //
        setContentView(binding?.root)

        //menulis versi dengan mengambil resource string app_version ke komponen textview yang ada di activity_splash.xml
        binding?.version?.text = "Version ${getString(R.string.app_version)}"

        //handler: mengambil thread (tugas atau cabang) dari background thread yang berkaitan dengan proses pembuatan activity
        Handler(Looper.getMainLooper()).postDelayed({

            //pindah activity ke MainActivity.java
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))

            //pindah activity ke MainActivity.java kemudian SplashActivity.java dihancurkan
            finishAffinity()

            //waktu tunggu untuk masuk menu beranda
        }, 2000)
    }
}
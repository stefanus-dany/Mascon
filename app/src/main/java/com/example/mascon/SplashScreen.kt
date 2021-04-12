package com.example.mascon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreen : AppCompatActivity() {
    private lateinit var iv_splash : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        iv_splash = findViewById(R.id.iv_splash)
        iv_splash.alpha = 0f
        iv_splash.animate().setDuration(1500).alpha(1f).withEndAction{
            startActivity(Intent(this, Login::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
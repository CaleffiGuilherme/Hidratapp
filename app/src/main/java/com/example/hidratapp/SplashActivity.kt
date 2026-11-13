package com.example.hidratapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cardLogo = findViewById<View>(R.id.cardLogo)
        val tvTitulo = findViewById<View>(R.id.tvTituloSplash)
        val tvSubtitulo = findViewById<View>(R.id.tvSubtituloSplash)

        // Estado inicial
        cardLogo.scaleX = 0.5f
        cardLogo.scaleY = 0.5f
        cardLogo.alpha = 0f

        tvTitulo.translationY = 40f
        tvTitulo.alpha = 0f

        tvSubtitulo.translationY = 40f
        tvSubtitulo.alpha = 0f

        // Animação da logo oficial (escala com overshoot + fade)
        cardLogo.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .alpha(1.0f)
            .setDuration(800)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()

        // Animação dos textos
        tvTitulo.animate()
            .translationY(0f)
            .alpha(1.0f)
            .setStartDelay(200)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()

        tvSubtitulo.animate()
            .translationY(0f)
            .alpha(1.0f)
            .setStartDelay(350)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Transição automática para a MainActivity após 1.8 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }, 1800)
    }
}

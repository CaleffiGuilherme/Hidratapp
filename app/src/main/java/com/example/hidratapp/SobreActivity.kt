package com.example.hidratapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SobreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sobre)
        aplicarVisual()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun fecharComAnimacao() {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        findViewById<ImageButton>(R.id.btnHeaderVoltar).setOnClickListener { fecharComAnimacao() }
        findViewById<android.view.View>(R.id.btnSobreRegras).setOnClickListener { startActivity(android.content.Intent(this, RegrasActivity::class.java)) }
        findViewById<Button>(R.id.btnVoltar).setOnClickListener { fecharComAnimacao() }
    }
}

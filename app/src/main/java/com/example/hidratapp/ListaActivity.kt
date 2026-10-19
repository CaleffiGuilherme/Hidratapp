package com.example.hidratapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaActivity : AppCompatActivity() {

    private lateinit var rvBebidas: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvBebidas = findViewById(R.id.rvBebidas)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        rvBebidas.layoutManager = LinearLayoutManager(this)

        findViewById<ImageButton>(R.id.btnHeaderVoltar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        val bebidas = BancoHelper(this).listar()
        if (bebidas.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvBebidas.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvBebidas.visibility = View.VISIBLE
            rvBebidas.adapter = BebidaAdapter(bebidas) { bebida ->
                val intent = Intent(this, DetalheBebidaActivity::class.java)
                intent.putExtra("bebida_id", bebida.id)
                startActivity(intent)
            }
        }
    }
}

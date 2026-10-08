package com.example.hidratapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetalheBebidaActivity : AppCompatActivity() {

    private var id = -1
    private var bebida: Bebida? = null

    private lateinit var tvNome: TextView
    private lateinit var tvInfo: TextView
    private lateinit var tvObservacao: TextView
    private lateinit var btnFavorito: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhe_bebida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        id = intent.getIntExtra("bebida_id", -1)

        tvNome = findViewById(R.id.tvDetalheNome)
        tvInfo = findViewById(R.id.tvDetalheInfo)
        tvObservacao = findViewById(R.id.tvDetalheObservacao)
        btnFavorito = findViewById(R.id.btnFavorito)
        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnExcluir = findViewById<Button>(R.id.btnExcluir)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        btnFavorito.setOnClickListener {
            val atual = bebida ?: return@setOnClickListener
            BancoHelper(this).atualizar(atual.copy(favorito = !atual.favorito))
            carregar()
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, CadastroBebidaActivity::class.java)
            intent.putExtra("bebida_id", id)
            startActivity(intent)
        }

        btnExcluir.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Excluir bebida?")
                .setMessage("Essa ação não pode ser desfeita.")
                .setPositiveButton("EXCLUIR") { _, _ ->
                    BancoHelper(this).excluir(id)
                    Toast.makeText(this, "Bebida excluída", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("CANCELAR", null)
                .show()
        }

        btnVoltar.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        carregar()
    }

    private fun carregar() {
        bebida = BancoHelper(this).buscarPorId(id)
        val b = bebida
        if (b == null) {
            finish()
            return
        }
        tvNome.text = "${b.tipo} • ${b.ml} ml"
        tvInfo.text = b.data
        tvObservacao.text = if (b.observacao.isEmpty()) "Sem observações" else b.observacao
        btnFavorito.text = if (b.favorito) "⭐ DESFAVORITAR" else "☆ FAVORITAR"
    }
}

package com.example.hidratapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class DetalheBebidaActivity : AppCompatActivity() {

    private var id = -1
    private var bebida: Bebida? = null

    private lateinit var tvNome: TextView
    private lateinit var tvInfo: TextView
    private lateinit var tvObservacao: TextView
    private lateinit var btnFavorito: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhe_bebida)
        aplicarVisual()
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
        val btnHeaderVoltar = findViewById<ImageButton>(R.id.btnHeaderVoltar)

        btnHeaderVoltar.setOnClickListener { finish() }

        btnFavorito.setOnClickListener {
            val atual = bebida ?: return@setOnClickListener
            val novoEstado = !atual.favorito
            if (runCatching { BancoHelper(this).use { it.atualizar(atual.copy(favorito = novoEstado)) } }.getOrDefault(0) != 1) {
                Toast.makeText(this, "Não foi possível atualizar o favorito", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Toast.makeText(
                this,
                if (novoEstado) "Adicionado aos favoritos" else "Removido dos favoritos",
                Toast.LENGTH_SHORT
            ).show()
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
                .setMessage("Essa ação removerá o registro do histórico.")
                .setPositiveButton("Excluir") { _, _ ->
                    if (runCatching { BancoHelper(this).use { it.excluir(id) } }.getOrDefault(0) != 1) {
                        Toast.makeText(this, "Não foi possível excluir", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }
                    Toast.makeText(this, "Registro excluído", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnVoltar.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        carregar()
    }

    private fun carregar() {
        bebida = runCatching { BancoHelper(this).use { it.buscarPorId(id) } }.getOrElse {
            Toast.makeText(this, "Não foi possível carregar a bebida", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val b = bebida
        if (b == null) {
            finish()
            return
        }
        tvNome.text = "${b.tipo} • ${b.ml} ml"
        tvInfo.text = "Registrado em ${b.data} ${b.hora.ifBlank { "(horário não registrado)" }}"
        tvObservacao.text = if (b.observacao.isEmpty()) "Nenhuma observação informada." else b.observacao
        if (b.favorito) {
            btnFavorito.text = "Desfavoritar"
            btnFavorito.setIconResource(R.drawable.ic_star)
        } else {
            btnFavorito.text = "Favoritar"
            btnFavorito.setIconResource(R.drawable.ic_star_border)
        }
    }
}

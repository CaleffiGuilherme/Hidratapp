package com.example.hidratapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvPercentual: TextView
    private lateinit var tvAviso: TextView
    private lateinit var cardAviso: View
    private lateinit var pbMeta: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTotal = findViewById(R.id.tvTotal)
        tvPercentual = findViewById(R.id.tvPercentual)
        tvAviso = findViewById(R.id.tvAviso)
        cardAviso = findViewById(R.id.cardAviso)
        pbMeta = findViewById(R.id.pbMeta)

        val btnNova = findViewById<Button>(R.id.btnNovaBebida)
        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)
        val btnSobre = findViewById<Button>(R.id.btnSobre)

        btnNova.setOnClickListener {
            startActivity(Intent(this, CadastroBebidaActivity::class.java))
        }
        btnLista.setOnClickListener {
            startActivity(Intent(this, ListaActivity::class.java))
        }
        btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        btnSobre.setOnClickListener {
            startActivity(Intent(this, SobreActivity::class.java))
        }

        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)
        if (!prefs.contains("peso")) {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarTela()
    }

    private fun atualizarTela() {
        val listaHoje = BancoHelper(this).listarPorData(Calculos.hoje())

        var totalMl = 0
        for (bebida in listaHoje) {
            if (!Calculos.ehAlcool(bebida.tipo)) {
                totalMl += bebida.ml
            }
        }

        val seguidas = Calculos.alcoolSeguidos(listaHoje)
        if (seguidas >= 2) {
            tvAviso.text = "Você registrou $seguidas bebidas alcoólicas seguidas. Que tal beber água agora?"
            cardAviso.visibility = View.VISIBLE
        } else {
            cardAviso.visibility = View.GONE
        }

        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)
        if (!prefs.contains("peso")) {
            tvTotal.text = "$totalMl ml"
            tvPercentual.text = "Preencha seu perfil para calcular sua meta diária."
            pbMeta.progress = 0
            return
        }

        val peso = prefs.getFloat("peso", 0f).toDouble()
        val idade = prefs.getInt("idade", 0)
        val masculino = prefs.getBoolean("masculino", false)
        val meta = Calculos.metaAgua(peso, idade, masculino)

        tvTotal.text = "$totalMl / $meta ml"
        pbMeta.max = meta
        pbMeta.progress = if (totalMl > meta) meta else totalMl
        val pct = if (meta > 0) (totalMl * 100 / meta) else 0
        tvPercentual.text = "$pct% da meta diária de hoje"
    }
}

package com.example.hidratapp

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
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

        // Botão Principal (+ Adicionar água)
        findViewById<Button>(R.id.btnNovaBebida).setOnClickListener {
            abrirActivity(CadastroBebidaActivity::class.java)
        }

        // Ícone Notificação
        findViewById<View>(R.id.btnNotificacoes).setOnClickListener {
            abrirActivity(DicasActivity::class.java)
        }

        // 5 Cards do Menu
        findViewById<View>(R.id.btnMenuMeta).setOnClickListener {
            abrirActivity(PerfilActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuEstatisticas).setOnClickListener {
            abrirActivity(ListaActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuLembretes).setOnClickListener {
            abrirActivity(DicasActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuHabitos).setOnClickListener {
            abrirActivity(SintomasActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuPerfil).setOnClickListener {
            abrirActivity(PerfilActivity::class.java)
        }

        // Área Secundária: Sobre o App
        findViewById<View>(R.id.btnSobre).setOnClickListener {
            abrirActivity(SobreActivity::class.java)
        }

        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)
        if (!prefs.contains("peso")) {
            abrirActivity(PerfilActivity::class.java)
        }
    }

    private fun abrirActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
            tvTotal.text = "$totalMl / 2000 ml"
            tvPercentual.text = "Preencha seu perfil"
            animarProgressBar(0)
            return
        }

        val peso = prefs.getFloat("peso", 0f).toDouble()
        val idade = prefs.getInt("idade", 0)
        val masculino = prefs.getBoolean("masculino", false)
        val esporte = prefs.getInt("esporte", 0)
        val meta = Calculos.metaAgua(peso, idade, masculino, esporte)

        tvTotal.text = "$totalMl / $meta ml"
        pbMeta.max = meta
        val targetProgress = if (totalMl > meta) meta else totalMl
        animarProgressBar(targetProgress)

        val pct = if (meta > 0) (totalMl * 100 / meta) else 0
        tvPercentual.text = "$pct% da meta"
    }

    private fun animarProgressBar(targetProgress: Int) {
        val animation = ObjectAnimator.ofInt(pbMeta, "progress", pbMeta.progress, targetProgress)
        animation.duration = 750
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}

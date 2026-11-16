package com.example.hidratapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class DicasActivity : DadosActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dicas)
        aplicarVisual()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnHeaderVoltar = findViewById<ImageButton>(R.id.btnHeaderVoltar)
        btnHeaderVoltar.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        carregarDicas()
    }

    private fun carregarDicas() {
        val containerDicas = findViewById<LinearLayout>(R.id.containerDicas)
        containerDicas.removeAllViews()

        dados({ listarPorData(Calculos.hoje()) to listarSintomasPorData(Calculos.hoje()) }) { (bebidasHoje, sintomasHoje) ->
        val listaDicas = DicasEngine.gerarDicas(this, bebidasHoje, sintomasHoje)

        val inflater = LayoutInflater.from(this)

        for (dica in listaDicas) {
            val itemView = inflater.inflate(R.layout.item_dica, containerDicas, false)

            val cardDica = itemView.findViewById<MaterialCardView>(R.id.cardDica)
            val ivIcone = itemView.findViewById<ImageView>(R.id.ivIconeDica)
            val tvTitulo = itemView.findViewById<TextView>(R.id.tvTituloDica)
            val tvDescricao = itemView.findViewById<TextView>(R.id.tvDescricaoDica)

            tvTitulo.text = dica.titulo
            tvDescricao.text = dica.descricao
            val expandir = itemView.findViewById<android.widget.Button>(R.id.btnExpandirDica)
            var aberto = false
            expandir.setOnClickListener {
                aberto = !aberto
                android.transition.TransitionManager.beginDelayedTransition(containerDicas, android.transition.AutoTransition().setDuration(180))
                tvDescricao.maxLines = if (aberto) Int.MAX_VALUE else 3
                tvDescricao.ellipsize = if (aberto) null else android.text.TextUtils.TruncateAt.END
                expandir.text = if (aberto) getString(R.string.dicas_recolher) else getString(R.string.item_dica_ler_dica)
            }

            when (dica.nivelAlerta) {
                NivelAlerta.PERIGO -> {
                    cardDica.setCardBackgroundColor(Color.parseColor("#FFF5F5"))
                    cardDica.strokeColor = Color.parseColor("#FEB2B2")
                    tvTitulo.setTextColor(Color.parseColor("#C53030"))
                    ivIcone.setImageResource(R.drawable.ic_warning)
                    ivIcone.setColorFilter(Color.parseColor("#C53030"))
                }
                NivelAlerta.ALERTA -> {
                    cardDica.setCardBackgroundColor(Color.parseColor("#FFF6ED"))
                    cardDica.strokeColor = Color.parseColor("#F0DFC9")
                    tvTitulo.setTextColor(Color.parseColor("#985421"))
                    ivIcone.setImageResource(R.drawable.ic_warning)
                    ivIcone.setColorFilter(Color.parseColor("#985421"))
                }
                NivelAlerta.INFO -> {
                    cardDica.setCardBackgroundColor(Color.parseColor("#EBF6FB"))
                    cardDica.strokeColor = Color.parseColor("#D9EAF5")
                    tvTitulo.setTextColor(Color.parseColor("#2879B8"))
                    ivIcone.setImageResource(R.drawable.ic_water_drop)
                    ivIcone.setColorFilter(Color.parseColor("#2879B8"))
                }
            }

            containerDicas.addView(itemView)
            configurarInteracoes(itemView)
        }
        }
    }
}

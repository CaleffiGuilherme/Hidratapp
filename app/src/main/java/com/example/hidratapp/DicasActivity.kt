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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class DicasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dicas)
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

        val banco = BancoHelper(this)
        val bebidasHoje = banco.listarPorData(Calculos.hoje())
        val sintomasHoje = banco.listarSintomasPorData(Calculos.hoje())

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

            when (dica.nivelAlerta) {
                NivelAlerta.PERIGO -> {
                    cardDica.setCardBackgroundColor(Color.parseColor("#FFF5F5"))
                    cardDica.strokeColor = Color.parseColor("#FEB2B2")
                    tvTitulo.setTextColor(Color.parseColor("#C53030"))
                    ivIcone.setImageResource(R.drawable.ic_warning)
                    ivIcone.setColorFilter(Color.parseColor("#C53030"))
                }
                NivelAlerta.ALERTA -> {
                    cardDica.setCardBackgroundColor(Color.parseColor("#FFFAF0"))
                    cardDica.strokeColor = Color.parseColor("#FBD38D")
                    tvTitulo.setTextColor(Color.parseColor("#C05621"))
                    ivIcone.setImageResource(R.drawable.ic_warning)
                    ivIcone.setColorFilter(Color.parseColor("#C05621"))
                }
                NivelAlerta.INFO -> {
                    cardDica.setCardBackgroundColor(Color.parseColor("#F0FDF4"))
                    cardDica.strokeColor = Color.parseColor("#BBF7D0")
                    tvTitulo.setTextColor(Color.parseColor("#15803D"))
                    ivIcone.setImageResource(R.drawable.ic_water_drop)
                    ivIcone.setColorFilter(Color.parseColor("#15803D"))
                }
            }

            containerDicas.addView(itemView)
        }
    }
}

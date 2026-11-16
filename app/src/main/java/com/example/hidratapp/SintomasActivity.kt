package com.example.hidratapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SintomasActivity : DadosActivity() {

    private val checkboxesSintomas = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sintomas)
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

        val containerCheckboxes = findViewById<LinearLayout>(R.id.containerCheckboxes)
        for (sintomaRes in SINTOMAS_DISPONIVEIS) {
            val sintomaNome = getString(sintomaRes)
            val cb = CheckBox(this).apply {
                text = sintomaNome
                textSize = 14f
                val d = resources.displayMetrics.density
                minHeight = (52*d).toInt()
                setPadding((12*d).toInt(),(4*d).toInt(),(12*d).toInt(),(4*d).toInt())
                setBackgroundResource(R.drawable.bg_choice)
                layoutParams = LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=(8*d).toInt() }
                setTextColor(getColor(R.color.text_primary))
            }
            checkboxesSintomas.add(cb)
            containerCheckboxes.addView(cb)
        }

        val btnSalvar = findViewById<Button>(R.id.btnSalvarSintoma)
        val etObs = findViewById<EditText>(R.id.etObsSintoma)
        val rgSede = findViewById<RadioGroup>(R.id.rgSede)

        btnSalvar.setOnClickListener {
            val selecionados = checkboxesSintomas.filter { it.isChecked }.map { it.text.toString() }
            if (selecionados.isEmpty()) {
                Toast.makeText(this, getString(R.string.sintomas_selecione_pelo_menos_um_sintoma), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idRgChecked = rgSede.checkedRadioButtonId
            val rbChecked = findViewById<RadioButton>(idRgChecked)
            val nivelSede = rbChecked?.text?.toString()?.toIntOrNull() ?: 3
            val obs = etObs.text.toString().trim()

            val novoSintoma = Sintoma(
                data = Calculos.hoje(),
                sintomasText = selecionados.joinToString(", "),
                nivelSede = nivelSede,
                observacao = obs
            )

            btnSalvar.isEnabled = false
            dados({ check(inserirSintoma(novoSintoma) > 0) }, finalizar = { btnSalvar.isEnabled = true }) {
            // Reseta form
            checkboxesSintomas.forEach { it.isChecked = false }
            etObs.setText("")
            rgSede.check(R.id.rbSede3)

            // Mostra alerta preventivo
            val mensagemFeedback = buildFeedbackMessage()
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.sintomas_relato_salvo_com_sucesso))
                .setMessage(mensagemFeedback)
                .setPositiveButton(getString(R.string.cadastro_bebida_entendido)) { dialog, _ ->
                    dialog.dismiss()
                    carregarHistorico()
                }
                .setCancelable(false)
                .show()
            }
        }

        carregarHistorico()
    }

    private fun buildFeedbackMessage(): String {
        return getString(R.string.sintomas_seus_sintomas_foram_registrados_este_registro_nao)
    }

    private fun carregarHistorico() {
        val containerHistorico = findViewById<LinearLayout>(R.id.containerHistoricoSintomas)
        containerHistorico.removeAllViews()

        dados({ listarSintomas() }) { listaSintomas ->
        if (listaSintomas.isEmpty()) {
            val tvVazio = TextView(this).apply {
                text = getString(R.string.sintomas_nenhum_sintoma_relatado_ainda)
                setTextColor(getColor(R.color.text_secondary))
                textSize = 14f
                setPadding(0, 16, 0, 16)
            }
            containerHistorico.addView(tvVazio)
            return@dados
        }

        val inflater = LayoutInflater.from(this)

        for (sintoma in listaSintomas) {
            val itemView = inflater.inflate(R.layout.item_sintoma, containerHistorico, false)

            val tvData = itemView.findViewById<TextView>(R.id.tvDataSintoma)
            val tvLista = itemView.findViewById<TextView>(R.id.tvListaSintomas)
            val tvNivelSede = itemView.findViewById<TextView>(R.id.tvNivelSede)
            val tvObs = itemView.findViewById<TextView>(R.id.tvObsSintoma)
            val btnDeletar = itemView.findViewById<ImageButton>(R.id.btnDeletarSintoma)

            tvData.text = getString(R.string.sintomas_data, sintoma.data)
            tvLista.text = sintoma.sintomasText
            tvNivelSede.text = getString(R.string.sintomas_nivel_de_sede_5, sintoma.nivelSede)

            if (sintoma.observacao.isNotEmpty()) {
                tvObs.text = getString(R.string.sintomas_obs, sintoma.observacao)
                tvObs.visibility = android.view.View.VISIBLE
            } else {
                tvObs.visibility = android.view.View.GONE
            }

            btnDeletar.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.sintomas_excluir_relato))
                    .setMessage(getString(R.string.sintomas_deseja_remover_este_relato_de_sintomas))
                    .setPositiveButton(getString(R.string.sintomas_sim)) { _, _ ->
                        dados({ check(excluirSintoma(sintoma.id) == 1) }) { carregarHistorico() }
                    }
                    .setNegativeButton(getString(R.string.sintomas_nao), null)
                    .show()
            }

            containerHistorico.addView(itemView)
        }
        }
    }
}

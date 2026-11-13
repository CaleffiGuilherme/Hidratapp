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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SintomasActivity : AppCompatActivity() {

    private val checkboxesSintomas = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sintomas)
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
        for (sintomaNome in SINTOMAS_DISPONIVEIS) {
            val cb = CheckBox(this).apply {
                text = sintomaNome
                textSize = 14f
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
                Toast.makeText(this, "Selecione pelo menos um sintoma", Toast.LENGTH_SHORT).show()
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

            val banco = BancoHelper(this)
            banco.inserirSintoma(novoSintoma)

            // Reseta form
            checkboxesSintomas.forEach { it.isChecked = false }
            etObs.setText("")
            rgSede.check(R.id.rbSede3)

            // Mostra alerta preventivo
            val mensagemFeedback = buildFeedbackMessage(selecionados, nivelSede)
            AlertDialog.Builder(this)
                .setTitle("Relato Salvo com Sucesso!")
                .setMessage(mensagemFeedback)
                .setPositiveButton("Entendido") { dialog, _ ->
                    dialog.dismiss()
                    carregarHistorico()
                }
                .setCancelable(false)
                .show()
        }

        carregarHistorico()
    }

    private fun buildFeedbackMessage(sintomas: List<String>, nivelSede: Int): String {
        val sb = java.lang.StringBuilder()
        sb.append("Seus sintomas foram registrados.\n\n")

        val temBocaSecaOuCabeca = sintomas.any { it.contains("Dor de cabeça") || it.contains("Boca seca") }
        val temUrinaEscura = sintomas.any { it.contains("Urina escura") }
        val temTonturaOuFadiga = sintomas.any { it.contains("Tontura") || it.contains("Fadiga") }

        if (nivelSede >= 4 || temBocaSecaOuCabeca || temUrinaEscura) {
            sb.append("⚠️ Recomendação Imediata: Seus relatos indicam possíveis sinais de desidratação leve a moderada.\n")
            sb.append("• Procure beber de 300ml a 500ml de água pura imediatamente.\n")
            sb.append("• Evite bebidas alcoólicas, café ou refrigerantes no momento.\n")
        } else {
            sb.append("💡 Dica: Mantenha garrafas de água por perto e continue monitorando como seu corpo se sente.")
        }

        if (temTonturaOuFadiga) {
            sb.append("\n• Descanse em um local arejado.")
        }

        return sb.toString()
    }

    private fun carregarHistorico() {
        val containerHistorico = findViewById<LinearLayout>(R.id.containerHistoricoSintomas)
        containerHistorico.removeAllViews()

        val banco = BancoHelper(this)
        val listaSintomas = banco.listarSintomas()

        if (listaSintomas.isEmpty()) {
            val tvVazio = TextView(this).apply {
                text = "Nenhum sintoma relatado ainda."
                setTextColor(getColor(R.color.text_secondary))
                textSize = 14f
                setPadding(0, 16, 0, 16)
            }
            containerHistorico.addView(tvVazio)
            return
        }

        val inflater = LayoutInflater.from(this)

        for (sintoma in listaSintomas) {
            val itemView = inflater.inflate(R.layout.item_sintoma, containerHistorico, false)

            val tvData = itemView.findViewById<TextView>(R.id.tvDataSintoma)
            val tvLista = itemView.findViewById<TextView>(R.id.tvListaSintomas)
            val tvNivelSede = itemView.findViewById<TextView>(R.id.tvNivelSede)
            val tvObs = itemView.findViewById<TextView>(R.id.tvObsSintoma)
            val btnDeletar = itemView.findViewById<ImageButton>(R.id.btnDeletarSintoma)

            tvData.text = "Data: ${sintoma.data}"
            tvLista.text = sintoma.sintomasText
            tvNivelSede.text = "Nível de sede: ${sintoma.nivelSede}/5"

            if (sintoma.observacao.isNotEmpty()) {
                tvObs.text = "Obs: ${sintoma.observacao}"
                tvObs.visibility = android.view.View.VISIBLE
            } else {
                tvObs.visibility = android.view.View.GONE
            }

            btnDeletar.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Excluir Relato")
                    .setMessage("Deseja remover este relato de sintomas?")
                    .setPositiveButton("Sim") { _, _ ->
                        banco.excluirSintoma(sintoma.id)
                        carregarHistorico()
                    }
                    .setNegativeButton("Não", null)
                    .show()
            }

            containerHistorico.addView(itemView)
        }
    }
}

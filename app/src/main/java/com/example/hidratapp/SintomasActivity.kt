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
        for (sintomaNome in SINTOMAS_DISPONIVEIS) {
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
            val salvo = runCatching { banco.use { it.inserirSintoma(novoSintoma) } }.getOrDefault(-1)
            if (salvo <= 0) {
                Toast.makeText(this, "Não foi possível salvar o relato. Tente novamente.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Reseta form
            checkboxesSintomas.forEach { it.isChecked = false }
            etObs.setText("")
            rgSede.check(R.id.rbSede3)

            // Mostra alerta preventivo
            val mensagemFeedback = buildFeedbackMessage()
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

    private fun buildFeedbackMessage(): String {
        return "Seus sintomas foram registrados.\n\nEste registro não é um diagnóstico. Os sintomas podem ter várias causas. Respeite orientações de saúde e eventuais restrições de líquidos. Se os sintomas forem intensos, persistirem ou piorarem, procure atendimento de saúde."
    }

    private fun carregarHistorico() {
        val containerHistorico = findViewById<LinearLayout>(R.id.containerHistoricoSintomas)
        containerHistorico.removeAllViews()

        val listaSintomas = runCatching { BancoHelper(this).use { it.listarSintomas() } }.getOrElse {
            Toast.makeText(this, "Não foi possível carregar os relatos", Toast.LENGTH_LONG).show()
            return
        }

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
                        if (runCatching { BancoHelper(this).use { it.excluirSintoma(sintoma.id) } }.getOrDefault(0) != 1) {
                            Toast.makeText(this, "Não foi possível excluir o relato", Toast.LENGTH_LONG).show()
                            return@setPositiveButton
                        }
                        carregarHistorico()
                    }
                    .setNegativeButton("Não", null)
                    .show()
            }

            containerHistorico.addView(itemView)
        }
    }
}

package com.example.hidratapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Calendar
import java.util.Locale

class CadastroBebidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro_bebida)
        aplicarVisual()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvTitulo = findViewById<TextView>(R.id.tvTituloCadastro)
        val btnHeaderVoltar = findViewById<ImageButton>(R.id.btnHeaderVoltar)
        val tipos = findViewById<ChipGroup>(R.id.chipTipos)
        val idsTipos = listOf(R.id.chipAgua, R.id.chipSuco, R.id.chipRefrigerante, R.id.chipCafe,
            R.id.chipEnergetico, R.id.chipCerveja, R.id.chipVinho, R.id.chipDestilado)
        val etMl = findViewById<EditText>(R.id.etMl)
        val etData = findViewById<EditText>(R.id.etData)
        val tilData = findViewById<TextInputLayout>(R.id.tilData)
        val etObservacao = findViewById<EditText>(R.id.etObservacao)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnHeaderVoltar.setOnClickListener { finish() }

        etData.setText(Calculos.hoje())

        fun abrirDatePicker() {
            val cal = Calendar.getInstance().apply {
                runCatching {
                    java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).apply { isLenient=false }.parse(etData.text.toString())
                }.getOrNull()?.let { time=it }
            }
            val dpd = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dataFormatada = String.format(Locale.ROOT, "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    etData.setText(dataFormatada)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        etData.setOnClickListener { abrirDatePicker() }
        tilData.setEndIconOnClickListener { abrirDatePicker() }

        fun adicionarMl(quantidade: Int) {
            val mlAtual = etMl.text.toString().trim().toIntOrNull() ?: 0
            val novoTotal = (mlAtual + quantidade).coerceAtMost(5000)
            etMl.setText(novoTotal.toString())
            etMl.setSelection(etMl.text.length)
        }

        findViewById<Button>(R.id.btn200).setOnClickListener { adicionarMl(200) }
        findViewById<Button>(R.id.btn300).setOnClickListener { adicionarMl(300) }
        findViewById<Button>(R.id.btn500).setOnClickListener { adicionarMl(500) }

        val id = intent.getIntExtra("bebida_id", -1)
        val bebidaEditada = if (id != -1) BancoHelper(this).use { it.buscarPorId(id) } else null

        if (bebidaEditada != null) {
            tvTitulo.text = "Um ajuste no seu registro."
            if (savedInstanceState == null) tipos.check(idsTipos[TIPOS.indexOf(bebidaEditada.tipo).coerceAtLeast(0)])
            etMl.setText(bebidaEditada.ml.toString())
            etData.setText(bebidaEditada.data)
            etObservacao.setText(bebidaEditada.observacao)
            btnSalvar.text = "Salvar alterações"
        }

        val etHora = findViewById<EditText>(R.id.etHoraConsumo)
        etHora.setText(if (bebidaEditada != null) bebidaEditada.hora else Calculos.horaAgora())
        etHora.setOnClickListener {
            val partes=etHora.text.toString().split(":")
            TimePickerDialog(this, { _,h,m -> etHora.setText("%02d:%02d".format(Locale.ROOT,h,m)) },
                partes.getOrNull(0)?.toIntOrNull() ?: 12,partes.getOrNull(1)?.toIntOrNull() ?: 0,true).show()
        }
        fun atualizarResumo() {
            val tipo=findViewById<Chip>(tipos.checkedChipId)?.text?.toString() ?: TIPOS.first()
            val ml=etMl.text.toString().toIntOrNull() ?: 0
            findViewById<TextView>(R.id.tvResumoRegistro).text=if (ml>0) "$tipo · $ml ml" else "$tipo · escolha a quantidade"
            btnSalvar.text=if (bebidaEditada != null) "Salvar alterações" else if (ml>0) "Registrar $ml ml" else "Salvar meu registro"
        }
        tipos.setOnCheckedStateChangeListener { _, _ -> atualizarResumo() }
        etMl.doAfterTextChanged { atualizarResumo() }
        atualizarResumo()
        btnCancelar.setOnClickListener { finish() }

        btnSalvar.setOnClickListener {
            val ml = etMl.text.toString().trim().toIntOrNull()
            val data = etData.text.toString().trim()
            val observacao = etObservacao.text.toString().trim()
            val tipo = findViewById<Chip>(tipos.checkedChipId)?.text?.toString() ?: TIPOS.first()

            if (ml == null || ml <= 0 || ml > 5000) {
                etMl.error = "Informe a quantidade em ml (1 a 5000)"
                return@setOnClickListener
            }
            if (data.isEmpty()) {
                etData.error = "Informe a data"
                return@setOnClickListener
            }

            val hora = etHora.text.toString()
            val instante = runCatching {
                java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ROOT).apply { isLenient = false }.parse("$data ${hora.ifBlank { "00:00" }}")
            }.getOrNull()
            if (instante == null || instante.time > System.currentTimeMillis()) {
                etData.error = "Informe uma data e horário válidos, sem ser no futuro"
                return@setOnClickListener
            }
            val banco = BancoHelper(this)

            if (bebidaEditada == null) {
                val salvo = runCatching { banco.inserir(Bebida(tipo = tipo, ml = ml, data = data, hora = hora, observacao = observacao)) }.getOrDefault(-1)
                if (salvo <= 0) {
                    Toast.makeText(this, "Não foi possível salvar. Tente novamente.", Toast.LENGTH_LONG).show()
                    banco.close()
                    return@setOnClickListener
                }

                val seguidas = Calculos.alcoolSeguidos(banco.listarPorData(Calculos.hoje()))
                if (seguidas >= 2) {
                    AlertDialog.Builder(this)
                        .setTitle("Atenção à Hidratação")
                        .setMessage("Você já registrou $seguidas bebidas alcoólicas seguidas. Água não neutraliza o álcool nem torna o consumo seguro. Evite dirigir após beber.")
                        .setPositiveButton("Entendido") { _, _ -> finish() }
                        .setCancelable(false)
                        .show()
                } else {
                    Toast.makeText(this, "Bebida salva com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                val atualizada = bebidaEditada.copy(
                    tipo = tipo, ml = ml, data = data, hora = hora, observacao = observacao
                )
                if (runCatching { banco.atualizar(atualizada) }.getOrDefault(0) != 1) {
                    Toast.makeText(this, "Não foi possível atualizar. Tente novamente.", Toast.LENGTH_LONG).show()
                    banco.close()
                    return@setOnClickListener
                }
                Toast.makeText(this, "Bebida atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            banco.close()
        }
    }
}

package com.example.hidratapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import java.util.Locale

class CadastroBebidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro_bebida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvTitulo = findViewById<TextView>(R.id.tvTituloCadastro)
        val btnHeaderVoltar = findViewById<ImageButton>(R.id.btnHeaderVoltar)
        val spTipo = findViewById<Spinner>(R.id.spTipo)
        val etMl = findViewById<EditText>(R.id.etMl)
        val etData = findViewById<EditText>(R.id.etData)
        val tilData = findViewById<TextInputLayout>(R.id.tilData)
        val etObservacao = findViewById<EditText>(R.id.etObservacao)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnHeaderVoltar.setOnClickListener { finish() }

        spTipo.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, TIPOS
        )
        etData.setText(Calculos.hoje())

        fun abrirDatePicker() {
            val cal = Calendar.getInstance()
            val dpd = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    etData.setText(dataFormatada)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
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
        val bebidaEditada = if (id != -1) BancoHelper(this).buscarPorId(id) else null

        if (bebidaEditada != null) {
            tvTitulo.text = "Editar Bebida"
            spTipo.setSelection(TIPOS.indexOf(bebidaEditada.tipo))
            etMl.setText(bebidaEditada.ml.toString())
            etData.setText(bebidaEditada.data)
            etObservacao.setText(bebidaEditada.observacao)
            btnSalvar.text = "Atualizar Bebida"
        }

        btnCancelar.setOnClickListener { finish() }

        btnSalvar.setOnClickListener {
            val ml = etMl.text.toString().trim().toIntOrNull()
            val data = etData.text.toString().trim()
            val observacao = etObservacao.text.toString().trim()
            val tipo = TIPOS[spTipo.selectedItemPosition]

            if (ml == null || ml <= 0 || ml > 5000) {
                etMl.error = "Informe a quantidade em ml (1 a 5000)"
                return@setOnClickListener
            }
            if (data.isEmpty()) {
                etData.error = "Informe a data"
                return@setOnClickListener
            }

            val banco = BancoHelper(this)

            if (bebidaEditada == null) {
                banco.inserir(Bebida(tipo = tipo, ml = ml, data = data, observacao = observacao))

                val seguidas = Calculos.alcoolSeguidos(banco.listarPorData(Calculos.hoje()))
                if (seguidas >= 2) {
                    AlertDialog.Builder(this)
                        .setTitle("Atenção à Hidratação")
                        .setMessage("Você já registrou $seguidas bebidas alcoólicas seguidas. Considere beber água para manter o equilíbrio.")
                        .setPositiveButton("Entendido") { _, _ -> finish() }
                        .setCancelable(false)
                        .show()
                } else {
                    Toast.makeText(this, "Bebida salva com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                val atualizada = bebidaEditada.copy(
                    tipo = tipo, ml = ml, data = data, observacao = observacao
                )
                banco.atualizar(atualizada)
                Toast.makeText(this, "Bebida atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

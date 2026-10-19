package com.example.hidratapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnHeaderVoltar = findViewById<ImageButton>(R.id.btnHeaderVoltar)
        val etPeso = findViewById<EditText>(R.id.etPeso)
        val etAltura = findViewById<EditText>(R.id.etAltura)
        val etIdade = findViewById<EditText>(R.id.etIdade)
        val rgSexo = findViewById<RadioGroup>(R.id.rgSexo)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarPerfil)

        btnHeaderVoltar.setOnClickListener { finish() }

        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)

        if (prefs.contains("peso")) {
            etPeso.setText(prefs.getFloat("peso", 0f).toString())
            etAltura.setText(prefs.getInt("altura", 0).toString())
            etIdade.setText(prefs.getInt("idade", 0).toString())
            if (prefs.getBoolean("masculino", false)) {
                rgSexo.check(R.id.rbMasculino)
            } else {
                rgSexo.check(R.id.rbFeminino)
            }
        }

        btnSalvar.setOnClickListener {
            val peso = etPeso.text.toString().trim().replace(",", ".").toDoubleOrNull()
            val altura = etAltura.text.toString().trim().toIntOrNull()
            val idade = etIdade.text.toString().trim().toIntOrNull()

            if (peso == null || peso < 20 || peso > 300) {
                etPeso.error = "Informe o peso em kg"
                return@setOnClickListener
            }
            if (altura == null || altura < 100 || altura > 250) {
                etAltura.error = "Informe a altura em cm"
                return@setOnClickListener
            }
            if (idade == null || idade < 5 || idade > 120) {
                etIdade.error = "Informe a idade"
                return@setOnClickListener
            }
            if (rgSexo.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Escolha o sexo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val masculino = rgSexo.checkedRadioButtonId == R.id.rbMasculino

            prefs.edit()
                .putFloat("peso", peso.toFloat())
                .putInt("altura", altura)
                .putInt("idade", idade)
                .putBoolean("masculino", masculino)
                .apply()

            val meta = Calculos.metaAgua(peso, idade, masculino)
            val imc = Calculos.imc(peso, altura)
            Toast.makeText(
                this,
                "Perfil salvo! Meta: $meta ml/dia • IMC: ${"%.1f".format(imc)}",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
}

package com.example.hidratapp

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PerfilActivity : AppCompatActivity() {

    private var currentStep = -1 // -1 = Resumo, 0 = Welcome, 1..4 = Steps

    private lateinit var layoutPerfilResumo: View
    private lateinit var layoutStep0Welcome: View
    private lateinit var layoutStep1Genero: View
    private lateinit var layoutStep2Fisico: View
    private lateinit var layoutStep3Esporte: View
    private lateinit var layoutStep4Saude: View

    private lateinit var tvEtapaIndicador: TextView
    private lateinit var pbWizard: ProgressBar

    // Resumo Views
    private lateinit var tvResumoMeta: TextView
    private lateinit var tvResumoIMC: TextView
    private lateinit var tvResumoPesoAltura: TextView
    private lateinit var tvResumoIdadeSexo: TextView
    private lateinit var tvResumoEsporte: TextView
    private lateinit var tvResumoSaude: TextView
    private lateinit var btnEditarPerfil: Button

    // Form Inputs
    private lateinit var etPeso: EditText
    private lateinit var etAltura: EditText
    private lateinit var etIdade: EditText
    private lateinit var rgSexo: RadioGroup
    private lateinit var rgEsporte: RadioGroup

    private lateinit var cbDiabetes: CheckBox
    private lateinit var cbHipertensao: CheckBox
    private lateinit var cbAlcoolismo: CheckBox
    private lateinit var cbPedrasRins: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        aplicarVisual()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnHeaderVoltar = findViewById<ImageButton>(R.id.btnHeaderVoltar)
        tvEtapaIndicador = findViewById(R.id.tvEtapaIndicador)
        pbWizard = findViewById(R.id.pbWizard)

        layoutPerfilResumo = findViewById(R.id.layoutPerfilResumo)
        layoutStep0Welcome = findViewById(R.id.layoutStep0Welcome)
        layoutStep1Genero = findViewById(R.id.layoutStep1Genero)
        layoutStep2Fisico = findViewById(R.id.layoutStep2Fisico)
        layoutStep3Esporte = findViewById(R.id.layoutStep3Esporte)
        layoutStep4Saude = findViewById(R.id.layoutStep4Saude)

        tvResumoMeta = findViewById(R.id.tvResumoMeta)
        tvResumoIMC = findViewById(R.id.tvResumoIMC)
        tvResumoPesoAltura = findViewById(R.id.tvResumoPesoAltura)
        tvResumoIdadeSexo = findViewById(R.id.tvResumoIdadeSexo)
        tvResumoEsporte = findViewById(R.id.tvResumoEsporte)
        tvResumoSaude = findViewById(R.id.tvResumoSaude)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)

        etPeso = findViewById(R.id.etPeso)
        etAltura = findViewById(R.id.etAltura)
        etIdade = findViewById(R.id.etIdade)
        rgSexo = findViewById(R.id.rgSexo)
        rgEsporte = findViewById(R.id.rgEsporte)

        cbDiabetes = findViewById(R.id.cbDiabetes)
        cbHipertensao = findViewById(R.id.cbHipertensao)
        cbAlcoolismo = findViewById(R.id.cbAlcoolismo)
        cbPedrasRins = findViewById(R.id.cbPedrasRins)

        // Botões de Navegação do Form
        findViewById<Button>(R.id.btnIniciarCadastro).setOnClickListener { irParaEtapa(1) }
        findViewById<Button>(R.id.btnStep1Proximo).setOnClickListener {
            if (validarStep1()) irParaEtapa(2)
        }
        findViewById<Button>(R.id.btnStep2Voltar).setOnClickListener { irParaEtapa(1) }
        findViewById<Button>(R.id.btnStep2Proximo).setOnClickListener {
            if (validarStep2()) irParaEtapa(3)
        }
        findViewById<Button>(R.id.btnStep3Voltar).setOnClickListener { irParaEtapa(2) }
        findViewById<Button>(R.id.btnStep3Proximo).setOnClickListener { irParaEtapa(4) }
        findViewById<Button>(R.id.btnStep4Voltar).setOnClickListener { irParaEtapa(3) }
        findViewById<Button>(R.id.btnSalvarPerfil).setOnClickListener { salvarPerfilFinal() }

        findViewById<Button>(R.id.btnConcluirPerfil).setOnClickListener { finish() }
        btnEditarPerfil.setOnClickListener {
            irParaEtapa(1)
        }

        btnHeaderVoltar.setOnClickListener {
            voltarOuFechar()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                voltarOuFechar()
            }
        })

        if (savedInstanceState == null) carregarDadosExistentes()
        else {
            val etapa = savedInstanceState.getInt("etapa", 0)
            if (etapa == -1) exibirResumo() else irParaEtapa(etapa, animar = false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("etapa", currentStep)
        super.onSaveInstanceState(outState)
    }

    private fun voltarOuFechar() {
        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)
        if (currentStep > 1) {
            irParaEtapa(currentStep - 1)
        } else if (currentStep == 1 && prefs.contains("peso")) {
            exibirResumo()
        } else if (currentStep == 1) {
            irParaEtapa(0)
        } else {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun carregarDadosExistentes() {
        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)
        if (prefs.contains("peso")) {
            preencherCamposFormulario(prefs)
            exibirResumo()
        } else {
            irParaEtapa(0, animar = false)
        }
    }

    private fun preencherCamposFormulario(prefs: android.content.SharedPreferences) {
        etPeso.setText(prefs.getFloat("peso", 0f).toString())
        etAltura.setText(prefs.getInt("altura", 0).toString())
        etIdade.setText(prefs.getInt("idade", 0).toString())

        if (prefs.getBoolean("masculino", false)) {
            rgSexo.check(R.id.rbMasculino)
        } else {
            rgSexo.check(R.id.rbFeminino)
        }

        when (prefs.getInt("esporte", 0)) {
            1 -> rgEsporte.check(R.id.rbEsporteLeve)
            2 -> rgEsporte.check(R.id.rbEsporteModerado)
            3 -> rgEsporte.check(R.id.rbEsporteIntenso)
            else -> rgEsporte.check(R.id.rbEsporteSedentario)
        }

        cbDiabetes.isChecked = prefs.getBoolean("diabetes", false)
        cbHipertensao.isChecked = prefs.getBoolean("hipertensao", false)
        cbAlcoolismo.isChecked = prefs.getBoolean("alcoolismo", false)
        cbPedrasRins.isChecked = prefs.getBoolean("pedras_rins", false)
    }

    private fun exibirResumo() {
        currentStep = -1
        findViewById<android.widget.ScrollView>(R.id.main).post { findViewById<android.widget.ScrollView>(R.id.main).smoothScrollTo(0,0) }
        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)

        pbWizard.visibility = View.GONE
        tvEtapaIndicador.text = getString(R.string.perfil_resumo)

        layoutStep0Welcome.visibility = View.GONE
        layoutStep1Genero.visibility = View.GONE
        layoutStep2Fisico.visibility = View.GONE
        layoutStep3Esporte.visibility = View.GONE
        layoutStep4Saude.visibility = View.GONE

        layoutPerfilResumo.visibility = View.VISIBLE

        val peso = prefs.getFloat("peso", 0f).toDouble()
        val altura = prefs.getInt("altura", 0)
        val idade = prefs.getInt("idade", 0)
        val masculino = prefs.getBoolean("masculino", false)
        val esporte = prefs.getInt("esporte", 0)

        val meta = PerfilRepository.meta(this)
        val imc = Calculos.imc(peso, altura)

        val classificacaoImc = when {
            idade < 20 -> getString(R.string.perfil_avaliacao_por_idade_requer_profissional)
            imc < 18.5 -> getString(R.string.perfil_abaixo_do_peso)
            imc < 25.0 -> getString(R.string.perfil_peso_normal)
            imc < 30.0 -> getString(R.string.perfil_sobrepeso)
            else -> getString(R.string.perfil_obesidade)
        }

        tvResumoMeta.text = getString(R.string.volume_dia, meta)
        tvResumoIMC.text = getString(R.string.perfil_imc_kg_m2, "%.1f".format(imc), classificacaoImc)

        val sexoStr = if (masculino) getString(R.string.perfil_masculino) else getString(R.string.perfil_feminino)
        tvResumoPesoAltura.text = getString(R.string.perfil_peso_kg_altura_cm, "%.1f".format(peso), altura)
        tvResumoIdadeSexo.text = getString(R.string.perfil_idade_anos_sexo, idade, sexoStr)

        val esporteStr = when (esporte) {
            1 -> getString(R.string.perfil_leve_1_2x_sem)
            2 -> getString(R.string.perfil_moderado_3_4x_sem)
            3 -> getString(R.string.perfil_intenso_atleta)
            else -> getString(R.string.perfil_sedentario_nao_pratico)
        }
        tvResumoEsporte.text = getString(R.string.perfil_atividade_fisica, esporteStr)

        val condicoes = mutableListOf<String>()
        if (prefs.getBoolean("diabetes", false)) condicoes.add(getString(R.string.perfil_diabetes))
        if (prefs.getBoolean("hipertensao", false)) condicoes.add(getString(R.string.perfil_hipertensao))
        if (prefs.getBoolean("alcoolismo", false)) condicoes.add(getString(R.string.perfil_historico_c_alcool))
        if (prefs.getBoolean("pedras_rins", false)) condicoes.add(getString(R.string.perfil_calculo_renal_rins))

        if (condicoes.isNotEmpty()) {
            tvResumoSaude.text = condicoes.joinToString(" • ")
        } else {
            tvResumoSaude.text = getString(R.string.perfil_nenhuma_condicao_especifica_cadastrada)
        }
    }

    private fun irParaEtapa(novaEtapa: Int, animar: Boolean = true) {
        currentStep = novaEtapa
        findViewById<android.widget.ScrollView>(R.id.main).post {
            findViewById<android.widget.ScrollView>(R.id.main).smoothScrollTo(0,0)
        }

        layoutPerfilResumo.visibility = View.GONE
        pbWizard.visibility = if (novaEtapa == 0) View.GONE else View.VISIBLE

        val layouts = listOf(
            layoutStep0Welcome,
            layoutStep1Genero,
            layoutStep2Fisico,
            layoutStep3Esporte,
            layoutStep4Saude
        )

        for (i in layouts.indices) {
            val view = layouts[i]
            if (i == novaEtapa) {
                view.visibility = View.VISIBLE
                if (animar) {
                    view.alpha = 0f
                    view.translationX = 60f
                    view.animate()
                        .alpha(1.0f)
                        .translationX(0f)
                        .setDuration(350)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                } else {
                    view.alpha = 1.0f
                    view.translationX = 0f
                }
            } else {
                view.visibility = View.GONE
            }
        }

        if (novaEtapa == 0) {
            tvEtapaIndicador.text = getString(R.string.perfil_boas_vindas)
            ObjectAnimator.ofInt(pbWizard, "progress", pbWizard.progress, 0).start()
        } else {
            tvEtapaIndicador.text = getString(R.string.perfil_etapa_de_4, novaEtapa)
            ObjectAnimator.ofInt(pbWizard, "progress", pbWizard.progress, novaEtapa).start()
        }
    }

    private fun validarStep1(): Boolean {
        if (rgSexo.checkedRadioButtonId == -1) {
            Toast.makeText(this, getString(R.string.perfil_por_favor_selecione_uma_opcao_de_sexo), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validarStep2(): Boolean {
        val peso = etPeso.text.toString().trim().replace(",", ".").toDoubleOrNull()
        val altura = etAltura.text.toString().trim().toIntOrNull()
        val idade = etIdade.text.toString().trim().toIntOrNull()

        if (peso == null || peso < 20 || peso > 300) {
            etPeso.error = getString(R.string.perfil_informe_seu_peso_em_kg_ex_70)
            return false
        }
        if (altura == null || altura < 100 || altura > 250) {
            etAltura.error = getString(R.string.perfil_informe_sua_altura_em_cm_ex_175)
            return false
        }
        if (idade == null || idade < 5 || idade > 120) {
            etIdade.error = getString(R.string.perfil_informe_sua_idade_ex_28)
            return false
        }
        return true
    }

    private fun salvarPerfilFinal() {
        if (!validarStep2()) {
            irParaEtapa(2)
            return
        }

        val peso = etPeso.text.toString().trim().replace(",", ".").toDouble()
        val altura = etAltura.text.toString().trim().toInt()
        val idade = etIdade.text.toString().trim().toInt()
        val masculino = rgSexo.checkedRadioButtonId == R.id.rbMasculino

        val nivelEsporte = when (rgEsporte.checkedRadioButtonId) {
            R.id.rbEsporteLeve -> 1
            R.id.rbEsporteModerado -> 2
            R.id.rbEsporteIntenso -> 3
            else -> 0
        }

        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat("peso", peso.toFloat())
            .putInt("altura", altura)
            .putInt("idade", idade)
            .putBoolean("masculino", masculino)
            .putInt("esporte", nivelEsporte)
            .putBoolean("diabetes", cbDiabetes.isChecked)
            .putBoolean("hipertensao", cbHipertensao.isChecked)
            .putBoolean("alcoolismo", cbAlcoolismo.isChecked)
            .putBoolean("pedras_rins", cbPedrasRins.isChecked)
            .apply()

        val meta = Calculos.metaAgua(peso, idade, masculino, nivelEsporte)
        val imc = Calculos.imc(peso, altura)

        Toast.makeText(
            this,
            getString(R.string.perfil_perfil_atualizado_com_sucesso_meta_ml_dia, meta, "%.1f".format(imc)),
            Toast.LENGTH_LONG
        ).show()

        exibirResumo()
    }
}

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Calendar
import java.util.Locale

class CadastroBebidaActivity : DadosActivity() {

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
        // 1. Monta as opções do catálogo; o XML item_tipo_bebida define a aparência.
        TipoBebida.entries.forEach { tipo ->
            val chip = layoutInflater.inflate(R.layout.item_tipo_bebida, tipos, false) as Chip
            chip.id = tipo.chipId
            chip.tag = tipo
            chip.text = tipo.nome(this)
            chip.setChipIconResource(tipo.icone)
            tipos.addView(chip)
        }
        tipos.check(TipoBebida.AGUA.chipId)
        fun tipoSelecionado(): TipoBebida =
            findViewById<Chip>(tipos.checkedChipId)?.tag as? TipoBebida ?: TipoBebida.AGUA
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
            val novoTotal = (mlAtual + quantidade).coerceAtMost(AjustesDoApp.VOLUME_MAXIMO_ML)
            etMl.setText(novoTotal.toString())
            etMl.setSelection(etMl.text.length)
        }

        listOf(R.id.btn200 to AjustesDoApp.ATALHO_PEQUENO_ML,
            R.id.btn300 to AjustesDoApp.ATALHO_MEDIO_ML,
            R.id.btn500 to AjustesDoApp.ATALHO_GRANDE_ML).forEach { (idBotao, quantidade) ->
            findViewById<Button>(idBotao).apply {
                text = getString(R.string.atalho_quantidade, quantidade)
                setOnClickListener { adicionarMl(quantidade) }
            }
        }

        val id = intent.getIntExtra("bebida_id", -1)
        var bebidaEditada: Bebida? = null
        var carregando = id != -1
        btnSalvar.isEnabled = !carregando

        val etHora = findViewById<EditText>(R.id.etHoraConsumo)
        etHora.setText(Calculos.horaAgora())
        etHora.setOnClickListener {
            val partes=etHora.text.toString().split(":")
            TimePickerDialog(this, { _,h,m -> etHora.setText("%02d:%02d".format(Locale.ROOT,h,m)) },
                partes.getOrNull(0)?.toIntOrNull() ?: 12,partes.getOrNull(1)?.toIntOrNull() ?: 0,true).show()
        }
        fun atualizarResumo() {
            val tipo=tipoSelecionado().nome(this)
            val ml=etMl.text.toString().toIntOrNull() ?: 0
            findViewById<TextView>(R.id.tvResumoRegistro).text=if (ml>0) getString(R.string.bebida_resumo, tipo, ml) else getString(R.string.cadastro_bebida_escolha_a_quantidade, tipo)
            btnSalvar.text=if (bebidaEditada != null) getString(R.string.cadastro_bebida_salvar_alteracoes) else if (ml>0) getString(R.string.cadastro_bebida_registrar_ml, ml) else getString(R.string.cadastro_bebida_salvar_meu_registro)
        }
        tipos.setOnCheckedStateChangeListener { _, _ -> atualizarResumo() }
        etMl.doAfterTextChanged { atualizarResumo() }
        atualizarResumo()
        btnCancelar.setOnClickListener { finish() }

        if (id != -1) dados({ buscarPorId(id) }) { registro ->
            if (registro == null) { finish(); return@dados }
            bebidaEditada = registro
            carregando = false
            btnSalvar.isEnabled = true
            tvTitulo.text = getString(R.string.cadastro_bebida_um_ajuste_no_seu_registro)
            if (savedInstanceState == null) {
                tipos.check((TipoBebida.doBanco(registro.tipo) ?: TipoBebida.AGUA).chipId)
                etMl.setText(registro.ml.toString())
                etData.setText(registro.data)
                etObservacao.setText(registro.observacao)
                etHora.setText(registro.hora)
            }
            atualizarResumo()
        }
        // 2. Lê os campos e valida. O limite fica em AjustesDoApp.
        btnSalvar.setOnClickListener {
            if (carregando) return@setOnClickListener
            val ml = etMl.text.toString().trim().toIntOrNull()
            val data = etData.text.toString().trim()
            val observacao = etObservacao.text.toString().trim()
            val tipo = tipoSelecionado().valorBanco

            if (ml == null || ml <= 0 || ml > AjustesDoApp.VOLUME_MAXIMO_ML) {
                etMl.error = getString(R.string.cadastro_bebida_informe_a_quantidade_em_ml_1_a, AjustesDoApp.VOLUME_MAXIMO_ML)
                return@setOnClickListener
            }
            if (data.isEmpty()) {
                etData.error = getString(R.string.cadastro_bebida_informe_a_data)
                return@setOnClickListener
            }

            val hora = etHora.text.toString()
            val instante = runCatching {
                java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ROOT).apply { isLenient = false }.parse("$data ${hora.ifBlank { "00:00" }}")
            }.getOrNull()
            if (instante == null || instante.time > System.currentTimeMillis()) {
                etData.error = getString(R.string.cadastro_bebida_informe_uma_data_e_horario_validos_sem)
                return@setOnClickListener
            }
            val anterior = bebidaEditada
            val registro = anterior?.copy(tipo=tipo, ml=ml, data=data, hora=hora, observacao=observacao)
                ?: Bebida(tipo=tipo, ml=ml, data=data, hora=hora, observacao=observacao)
            btnSalvar.isEnabled = false
            // 3. Grava pelo repository fora da thread da tela.
            dados({
                if (anterior == null) check(inserir(registro) > 0) else check(atualizar(registro) == 1)
                Calculos.alcoolSeguidos(listarPorData(Calculos.hoje()))
            }, finalizar = { btnSalvar.isEnabled = true }) { seguidas ->
                if (anterior == null && seguidas >= 2) {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.cadastro_bebida_atencao_a_hidratacao))
                        .setMessage(getString(R.string.cadastro_bebida_voce_ja_registrou_bebidas_alcoolicas_seguidas_agua, seguidas))
                        .setPositiveButton(getString(R.string.cadastro_bebida_entendido)) { _, _ -> finish() }
                        .setCancelable(false).show()
                } else {
                    Toast.makeText(this, if (anterior == null) getString(R.string.cadastro_bebida_bebida_salva_com_sucesso) else getString(R.string.cadastro_bebida_bebida_atualizada_com_sucesso), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}

package com.example.hidratapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class DetalheBebidaActivity : DadosActivity() {

    private var id = -1
    private var bebida: Bebida? = null
    private var excluida: Bebida? = null
    private var prazoDesfazer = 0L
    private var snackbar: com.google.android.material.snackbar.Snackbar? = null

    private lateinit var tvNome: TextView
    private lateinit var tvInfo: TextView
    private lateinit var tvObservacao: TextView
    private lateinit var btnFavorito: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhe_bebida)
        aplicarVisual()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        id = intent.getIntExtra("bebida_id", -1)
        excluida = savedInstanceState?.getSerializable("excluida") as? Bebida
        prazoDesfazer = savedInstanceState?.getLong("prazoDesfazer") ?: 0L

        tvNome = findViewById(R.id.tvDetalheNome)
        tvInfo = findViewById(R.id.tvDetalheInfo)
        tvObservacao = findViewById(R.id.tvDetalheObservacao)
        btnFavorito = findViewById(R.id.btnFavorito)
        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnExcluir = findViewById<Button>(R.id.btnExcluir)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        val btnHeaderVoltar = findViewById<ImageButton>(R.id.btnHeaderVoltar)

        btnHeaderVoltar.setOnClickListener { finish() }

        btnFavorito.setOnClickListener {
            val atual = bebida ?: return@setOnClickListener
            val novoEstado = !atual.favorito
            btnFavorito.isEnabled = false
            dados({ atualizar(atual.copy(favorito = novoEstado)) }, finalizar = { btnFavorito.isEnabled = true }) { linhas ->
                if (linhas != 1) Toast.makeText(this, getString(R.string.detalhe_bebida_nao_foi_possivel_atualizar_o_favorito), Toast.LENGTH_LONG).show()
                carregar()
            }
        }

        btnEditar.setOnClickListener {
            val intent = Intent(this, CadastroBebidaActivity::class.java)
            intent.putExtra("bebida_id", id)
            startActivity(intent)
        }

        btnExcluir.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.detalhe_bebida_excluir_bebida))
                .setMessage(getString(R.string.detalhe_bebida_essa_acao_removera_o_registro_do_historico))
                .setPositiveButton(getString(R.string.item_sintoma_excluir)) { _, _ ->
                    val registro = bebida ?: return@setPositiveButton
                    btnExcluir.isEnabled = false
                    dados({ excluir(id) }, finalizar = { btnExcluir.isEnabled = true }) { linhas ->
                        if (linhas == 1) {
                            excluida = registro
                            prazoDesfazer = android.os.SystemClock.elapsedRealtime() + AjustesDoApp.DESFAZER_EXCLUSAO_MS
                            mostrarDesfazer()
                        }
                    }
                }

                .setNegativeButton(getString(R.string.cadastro_bebida_cancelar), null)
                .show()
        }

        btnVoltar.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (excluida != null) dados({ buscarPorId(id) }) { existente ->
            if (existente != null) {
                excluida = null
                findViewById<Button>(R.id.btnEditar).isEnabled = true
                findViewById<Button>(R.id.btnExcluir).isEnabled = true
                btnFavorito.isEnabled = true
                carregar()
            } else mostrarDesfazer()
        } else carregar()
    }

    private fun carregar() {
        dados({ buscarPorId(id) }) { registro ->
            bebida = registro
            if (registro == null) finish() else exibir(registro)
        }
    }

    private fun exibir(b: Bebida) {
        tvNome.text = getString(R.string.bebida_titulo, TipoBebida.nomeExibido(b.tipo, this), b.ml)
        tvInfo.text = getString(R.string.detalhe_bebida_registrado_em, b.data, b.hora.ifBlank { getString(R.string.detalhe_bebida_horario_nao_registrado) })
        tvObservacao.text = if (b.observacao.isEmpty()) getString(R.string.detalhe_bebida_nenhuma_observacao_informada) else b.observacao
        if (b.favorito) {
            btnFavorito.text = getString(R.string.detalhe_bebida_desfavoritar)
            btnFavorito.setIconResource(R.drawable.ic_star)
        } else {
            btnFavorito.text = getString(R.string.detalhe_bebida_favoritar)
            btnFavorito.setIconResource(R.drawable.ic_star_border)
        }
    }

    private fun mostrarDesfazer() {
        val registro = excluida ?: return
        exibir(registro)
        val restante = prazoDesfazer - android.os.SystemClock.elapsedRealtime()
        if (restante <= 0) { finish(); return }
        findViewById<Button>(R.id.btnEditar).isEnabled = false
        findViewById<Button>(R.id.btnExcluir).isEnabled = false
        btnFavorito.isEnabled = false
        snackbar?.dismiss()
        snackbar = com.google.android.material.snackbar.Snackbar.make(
            findViewById(android.R.id.content), getString(R.string.detalhe_bebida_registro_excluido), restante.toInt()
        ).setBackgroundTint(getColor(R.color.text_primary))
            .setTextColor(getColor(R.color.white))
            .setActionTextColor(getColor(R.color.blue_pastel))
            .setAction(getString(R.string.detalhe_bebida_desfazer)) {
            dados({ runCatching { restaurar(registro) } }) { resultado ->
                if (resultado.isSuccess) {
                    excluida = null
                    findViewById<Button>(R.id.btnEditar).isEnabled = true
                    findViewById<Button>(R.id.btnExcluir).isEnabled = true
                    btnFavorito.isEnabled = true
                    carregar()
                } else {
                    Toast.makeText(this, getString(R.string.detalhe_bebida_nao_foi_possivel_desfazer), Toast.LENGTH_LONG).show()
                    prazoDesfazer = android.os.SystemClock.elapsedRealtime() + AjustesDoApp.DESFAZER_EXCLUSAO_MS
                    mostrarDesfazer()
                }
            }
        }.addCallback(object : com.google.android.material.snackbar.Snackbar.Callback() {
            override fun onDismissed(bar: com.google.android.material.snackbar.Snackbar?, event: Int) {
                if (event == DISMISS_EVENT_TIMEOUT && excluida != null && !isChangingConfigurations) finish()
            }
        }).also { it.show() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("excluida", excluida)
        outState.putLong("prazoDesfazer", prazoDesfazer)
        super.onSaveInstanceState(outState)
    }
}

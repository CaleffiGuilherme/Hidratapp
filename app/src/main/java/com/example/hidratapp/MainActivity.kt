package com.example.hidratapp

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : DadosActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvPercentual: TextView
    private lateinit var tvAviso: TextView
    private lateinit var cardAviso: View
    private lateinit var pbMeta: ProgressBar
    private var paginasConsumo = listOf(PaginaConsumo(TipoBebida.AGUA.valorBanco,0))
    private var bebidaSelecionada = TipoBebida.AGUA.valorBanco
    private var animacaoProgresso: ObjectAnimator? = null
    private val relogio = android.os.Handler(android.os.Looper.getMainLooper())
    internal val virada = Runnable { atualizarTela(); agendarVirada() }
    private val mudancaHorario = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) { atualizarTela(); agendarVirada() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        aplicarVisual()
        atualizarData()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTotal = findViewById(R.id.tvTotal)
        tvPercentual = findViewById(R.id.tvPercentual)
        tvAviso = findViewById(R.id.tvAviso)
        cardAviso = findViewById(R.id.cardAviso)
        pbMeta = findViewById(R.id.pbMeta)

        bebidaSelecionada = savedInstanceState?.getString("bebidaSelecionada") ?: TipoBebida.AGUA.valorBanco
        findViewById<SwipeConsumoLayout>(R.id.areaConsumo).aoDeslizar = { moverBebida(it) }
        findViewById<View>(R.id.btnBebidaAnterior).setOnClickListener { moverBebida(-1) }
        findViewById<View>(R.id.btnProximaBebida).setOnClickListener { moverBebida(1) }
        findViewById<View>(R.id.infoConsumo).accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        Lembretes.agendar(this)

        // Abre o cadastro completo de qualquer bebida.
        findViewById<Button>(R.id.btnNovaBebida).setOnClickListener {
            abrirActivity(CadastroBebidaActivity::class.java)
        }

        // Ícone Notificação
        findViewById<View>(R.id.btnNotificacoes).setOnClickListener {
            abrirActivity(DicasActivity::class.java)
        }

        // 5 Cards do Menu
        findViewById<View>(R.id.btnMenuMeta).setOnClickListener {
            abrirActivity(PerfilActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuEstatisticas).setOnClickListener {
            abrirActivity(ListaActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuLembretes).setOnClickListener {
            abrirActivity(LembretesActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuHabitos).setOnClickListener {
            abrirActivity(SintomasActivity::class.java)
        }
        findViewById<View>(R.id.btnMenuPerfil).setOnClickListener {
            abrirActivity(PerfilActivity::class.java)
        }

        // Área Secundária: Sobre o App
        findViewById<View>(R.id.btnSobre).setOnClickListener {
            abrirActivity(SobreActivity::class.java)
        }

        findViewById<View>(R.id.btnRegras).setOnClickListener { abrirActivity(RegrasActivity::class.java) }
        // Atalhos de água: valor e rótulo vêm de AjustesDoApp.
        listOf(R.id.agua200 to AjustesDoApp.ATALHO_PEQUENO_ML, R.id.agua300 to AjustesDoApp.ATALHO_MEDIO_ML, R.id.agua500 to AjustesDoApp.ATALHO_GRANDE_ML).forEach { (id, ml) ->
            findViewById<Button>(id).text = getString(R.string.atalho_quantidade, ml)
            findViewById<View>(id).setOnClickListener {
                it.isEnabled = false
                dados({ inserir(Bebida(tipo = TipoBebida.AGUA.valorBanco, ml = ml,
                    data = Calculos.hoje(), hora = Calculos.horaAgora(), observacao = getString(R.string.main_registro_rapido))) },
                    finalizar = { it.isEnabled = true }) { registro ->
                    if (registro <= 0) android.widget.Toast.makeText(this, getString(R.string.main_nao_foi_possivel_salvar), android.widget.Toast.LENGTH_LONG).show()
                    else {
                        bebidaSelecionada = TipoBebida.AGUA.valorBanco
                        atualizarTela()
                        com.google.android.material.snackbar.Snackbar.make(findViewById(R.id.main), getString(R.string.main_ml_registrados, ml), AjustesDoApp.DESFAZER_REGISTRO_MS)
                            .setBackgroundTint(getColor(R.color.text_primary))
                            .setTextColor(getColor(R.color.white))
                            .setActionTextColor(getColor(R.color.blue_pastel))
                            .setAnchorView(R.id.layoutBottomButton)
                            .setAction(getString(R.string.detalhe_bebida_desfazer)) { dados({ excluir(registro.toInt()) }) { atualizarTela() } }.show()
                    }
                }
            }
        }
        val prefs = getSharedPreferences("perfil", Context.MODE_PRIVATE)
        if (!prefs.contains("peso")) {
            abrirActivity(PerfilActivity::class.java)
        }
    }

    private fun abrirActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onResume() {
        super.onResume()
        atualizarTela()
        agendarVirada()
    }

    override fun onStart() {
        super.onStart()
        val filtro = android.content.IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        androidx.core.content.ContextCompat.registerReceiver(this, mudancaHorario, filtro,
            androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        relogio.removeCallbacks(virada)
        unregisterReceiver(mudancaHorario)
        findViewById<View>(R.id.areaConsumo).apply { animate().cancel(); scaleX=1f; scaleY=1f }
        super.onStop()
    }

    private fun atualizarData() {
        findViewById<TextView>(R.id.tvDataHoje).text = java.text.SimpleDateFormat(
            "EEEE, d 'de' MMMM", java.util.Locale.forLanguageTag("pt-BR")
        ).format(java.util.Date(RelogioDoApp.agora())).uppercase(java.util.Locale.forLanguageTag("pt-BR"))
    }

    private fun agendarVirada() {
        relogio.removeCallbacks(virada)
        val agora = RelogioDoApp.agora()
        relogio.postDelayed(virada, (RotinaDiaria.proximaMeiaNoite(agora)-agora+100).coerceAtLeast(100))
    }

    private fun celebrarSeNecessario(registros: List<Bebida>) {
        if (!lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) return
        val prefs = getSharedPreferences("celebracao", Context.MODE_PRIVATE)
        val dia = Calculos.hoje()
        if (!RotinaDiaria.celebrar(dia, prefs.getString("ultimoDia", null),
                Calculos.totalValido(registros), PerfilRepository.meta(this))) return
        prefs.edit().putString("ultimoDia", dia).apply()
        findViewById<View>(R.id.areaConsumo).announceForAccessibility(getString(R.string.meta_celebracao))
        findViewById<TextView>(R.id.tvIncentivo).text = getString(R.string.meta_celebracao)
        if (android.os.Build.VERSION.SDK_INT >= 26 && !android.animation.ValueAnimator.areAnimatorsEnabled()) return
        findViewById<View>(R.id.areaConsumo).apply {
            animate().scaleX(1.025f).scaleY(1.025f).setDuration(AjustesDoApp.ANIMACAO_COMEMORACAO_MS).withEndAction {
                animate().scaleX(1f).scaleY(1f).setDuration(AjustesDoApp.ANIMACAO_COMEMORACAO_MS).start()
            }.start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("bebidaSelecionada",bebidaSelecionada)
        super.onSaveInstanceState(outState)
    }

    private fun moverBebida(direcao: Int) {
        val atual = paginasConsumo.indexOfFirst { it.chave == bebidaSelecionada }.coerceAtLeast(0)
        val proximo = (atual + direcao).coerceIn(0,paginasConsumo.lastIndex)
        if (atual == proximo) return
        bebidaSelecionada = paginasConsumo[proximo].chave
        exibirConsumo(direcao)
    }

    private fun atualizarTela() {
        val dia = Calculos.hoje()
        atualizarData()
        dados({ listarPorData(dia) }) { listaHoje ->
        if (dia != Calculos.hoje()) { atualizarTela(); return@dados }
        val seguidas = Calculos.alcoolSeguidos(listaHoje)
        if (seguidas >= 2) {
            tvAviso.text = getString(R.string.main_voce_registrou_bebidas_alcoolicas_seguidas_que_tal, seguidas)
            cardAviso.visibility = View.VISIBLE
        } else {
            cardAviso.visibility = View.GONE
        }

        paginasConsumo = ConsumoDoDia.paginas(listaHoje)
        if (paginasConsumo.none { it.chave == bebidaSelecionada }) bebidaSelecionada = TipoBebida.AGUA.valorBanco
        exibirConsumo()
            celebrarSeNecessario(listaHoje)
        }
    }

    private fun exibirConsumo(direcao: Int = 0) {
        val indice = paginasConsumo.indexOfFirst { it.chave == bebidaSelecionada }.coerceAtLeast(0)
        val pagina = paginasConsumo[indice]
        val meta = PerfilRepository.meta(this)
        val alcool = pagina.tipo?.let { Calculos.ehAlcool(it) } ?: false
        val titulo = findViewById<TextView>(R.id.tvBebidaAtual)
        titulo.text = pagina.titulo(this@MainActivity)
        val icone = if (pagina.tipo == null) R.drawable.ic_chart_bar
            else TipoBebida.doBanco(pagina.tipo)?.icone ?: R.drawable.ic_drink_glass
        findViewById<android.widget.ImageView>(R.id.ivBebidaAtual).setImageResource(icone)
        tvTotal.text = getString(R.string.volume_formatado, java.text.NumberFormat.getIntegerInstance(java.util.Locale.forLanguageTag("pt-BR")).format(pagina.ml))
        val descricao = findViewById<TextView>(R.id.tvMetaDia)
        val incentivo = findViewById<TextView>(R.id.tvIncentivo)
        animacaoProgresso?.cancel()
        pbMeta.max = meta.coerceAtLeast(1)
        when {
            alcool -> {
                descricao.text = getString(R.string.main_consumo_registrado_hoje)
                tvPercentual.text = getString(R.string.main_fora_da_meta)
                incentivo.text = getString(R.string.main_bebidas_alcoolicas_ficam_apenas_no_historico)
                animarProgressBar(0)
            }
            meta <= 0 -> {
                descricao.text = getString(R.string.main_vamos_definir_sua_meta)
                tvPercentual.text = getString(R.string.main_preencha_seu_perfil)
                incentivo.text = getString(R.string.main_seus_registros_de_hoje_separados_por_bebida)
                animarProgressBar(0)
            }
            else -> {
                descricao.text = if (pagina.tipo == null) getString(R.string.main_de_ml_para_hoje, meta) else getString(R.string.main_da_meta_total_de_ml, meta)
                val pct = pagina.ml.toLong() * 100 / meta
                tvPercentual.text = if (pagina.tipo == null) getString(R.string.main_da_meta, pct) else getString(R.string.main_da_meta_total, pct)
                incentivo.text = when {
                    pagina.ml >= meta -> getString(R.string.main_meta_alcancada_um_cuidado_para_celebrar)
                    pagina.ml == 0 && pagina.tipo == TipoBebida.AGUA.valorBanco -> getString(R.string.main_seu_proximo_gole_pode_ser_agora)
                    pagina.tipo == TipoBebida.AGUA.valorBanco || pagina.tipo == null -> getString(R.string.main_cada_gole_conta_voce_esta_no_seu)
                    else -> getString(R.string.main_de_hoje_veja_tambem_seu_total_diario, pagina.titulo(this@MainActivity))
                }
                animarProgressBar(pagina.ml.coerceAtMost(meta))
            }
        }
        findViewById<View>(R.id.navegacaoBebidas).visibility = if (paginasConsumo.size>1) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.tvPaginaBebida).text = getString(R.string.main_de_deslize_para_os_lados, indice+1, paginasConsumo.size)
        findViewById<View>(R.id.btnBebidaAnterior).apply {
            isEnabled = indice>0;alpha=if (isEnabled) 1f else .3f
            contentDescription = if (isEnabled) getString(R.string.main_ver, paginasConsumo[indice-1].titulo(this@MainActivity)) else getString(R.string.main_primeira_bebida)
        }
        findViewById<View>(R.id.btnProximaBebida).apply {
            isEnabled = indice<paginasConsumo.lastIndex;alpha=if (isEnabled) 1f else .3f
            contentDescription = if (isEnabled) getString(R.string.main_ver, paginasConsumo[indice+1].titulo(this@MainActivity)) else getString(R.string.main_ultima_pagina)
        }
        if (direcao != 0) findViewById<View>(R.id.infoConsumo).apply {
            animate().cancel();alpha=.35f;translationX=direcao*16*resources.displayMetrics.density
            animate().alpha(1f).translationX(0f).setDuration(AjustesDoApp.ANIMACAO_TROCA_MS).start()
        }
    }

    private fun animarProgressBar(targetProgress: Int) {
        animacaoProgresso = ObjectAnimator.ofInt(pbMeta, "progress", pbMeta.progress, targetProgress).apply {
            duration = AjustesDoApp.ANIMACAO_PROGRESSO_MS
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    override fun onDestroy() {
        animacaoProgresso?.cancel()
        super.onDestroy()
    }
}

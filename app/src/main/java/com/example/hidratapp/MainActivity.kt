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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvPercentual: TextView
    private lateinit var tvAviso: TextView
    private lateinit var cardAviso: View
    private lateinit var pbMeta: ProgressBar
    private var paginasConsumo = listOf(PaginaConsumo("Água",0))
    private var bebidaSelecionada = "Água"
    private var animacaoProgresso: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        aplicarVisual()
        findViewById<TextView>(R.id.tvDataHoje).text = java.text.SimpleDateFormat("EEEE, d 'de' MMMM", java.util.Locale.forLanguageTag("pt-BR")).format(java.util.Date()).uppercase(java.util.Locale.forLanguageTag("pt-BR"))
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

        bebidaSelecionada = savedInstanceState?.getString("bebidaSelecionada") ?: "Água"
        findViewById<SwipeConsumoLayout>(R.id.areaConsumo).aoDeslizar = { moverBebida(it) }
        findViewById<View>(R.id.btnBebidaAnterior).setOnClickListener { moverBebida(-1) }
        findViewById<View>(R.id.btnProximaBebida).setOnClickListener { moverBebida(1) }
        findViewById<View>(R.id.infoConsumo).accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        Lembretes.agendar(this)

        // Botão Principal (+ Adicionar água)
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
        listOf(R.id.agua200 to 200, R.id.agua300 to 300, R.id.agua500 to 500).forEach { (id, ml) ->
            findViewById<View>(id).setOnClickListener {
                val registro = runCatching { BancoHelper(this).use { it.inserir(Bebida(tipo = "Água", ml = ml,
                    data = Calculos.hoje(), hora = Calculos.horaAgora(), observacao = "Registro rápido")) } }.getOrDefault(-1)
                if (registro <= 0) android.widget.Toast.makeText(this, "Não foi possível salvar. Tente novamente.", android.widget.Toast.LENGTH_LONG).show()
                else {
                    bebidaSelecionada = "Água"
                    atualizarTela()
                    com.google.android.material.snackbar.Snackbar.make(findViewById(R.id.main), "$ml ml registrados", 6000)
                        .setBackgroundTint(getColor(R.color.text_primary))
                        .setTextColor(getColor(R.color.white))
                        .setActionTextColor(getColor(R.color.blue_pastel))
                        .setAnchorView(R.id.layoutBottomButton)
                        .setAction("Desfazer") {
                            val removido = runCatching { BancoHelper(this).use { it.excluir(registro.toInt()) } }.getOrDefault(0)
                            if (removido == 0) android.widget.Toast.makeText(this, "Não foi possível desfazer", android.widget.Toast.LENGTH_LONG).show()
                            atualizarTela()
                        }.show()
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
        val listaHoje = runCatching { BancoHelper(this).use { it.listarPorData(Calculos.hoje()) } }.getOrElse {
            android.widget.Toast.makeText(this, "Não foi possível carregar seus registros", android.widget.Toast.LENGTH_LONG).show()
            return
        }

        val seguidas = Calculos.alcoolSeguidos(listaHoje)
        if (seguidas >= 2) {
            tvAviso.text = "Você registrou $seguidas bebidas alcoólicas seguidas. Que tal beber água agora?"
            cardAviso.visibility = View.VISIBLE
        } else {
            cardAviso.visibility = View.GONE
        }

        paginasConsumo = ConsumoDoDia.paginas(listaHoje)
        if (paginasConsumo.none { it.chave == bebidaSelecionada }) bebidaSelecionada = "Água"
        exibirConsumo()
    }

    private fun exibirConsumo(direcao: Int = 0) {
        val indice = paginasConsumo.indexOfFirst { it.chave == bebidaSelecionada }.coerceAtLeast(0)
        val pagina = paginasConsumo[indice]
        val meta = PerfilRepository.meta(this)
        val alcool = pagina.tipo?.let { Calculos.ehAlcool(it) } ?: false
        val titulo = findViewById<TextView>(R.id.tvBebidaAtual)
        titulo.text = pagina.titulo
        val icone = when (pagina.tipo) {
            null -> R.drawable.ic_chart_bar
            "Água" -> R.drawable.ic_water_drop
            "Café" -> R.drawable.ic_coffee
            else -> R.drawable.ic_drink_glass
        }
        findViewById<android.widget.ImageView>(R.id.ivBebidaAtual).setImageResource(icone)
        tvTotal.text = "${java.text.NumberFormat.getIntegerInstance(java.util.Locale.forLanguageTag("pt-BR")).format(pagina.ml)} ml"
        val descricao = findViewById<TextView>(R.id.tvMetaDia)
        val incentivo = findViewById<TextView>(R.id.tvIncentivo)
        animacaoProgresso?.cancel()
        pbMeta.max = meta.coerceAtLeast(1)
        when {
            alcool -> {
                descricao.text = "Consumo registrado hoje"
                tvPercentual.text = "Fora da meta"
                incentivo.text = "Bebidas alcoólicas ficam apenas no histórico."
                animarProgressBar(0)
            }
            meta <= 0 -> {
                descricao.text = "Vamos definir sua meta?"
                tvPercentual.text = "Preencha seu perfil"
                incentivo.text = "Seus registros de hoje, separados por bebida."
                animarProgressBar(0)
            }
            else -> {
                descricao.text = if (pagina.tipo == null) "de $meta ml para hoje" else "da meta total de $meta ml"
                val pct = pagina.ml.toLong() * 100 / meta
                tvPercentual.text = if (pagina.tipo == null) "$pct% da meta" else "$pct% da meta total"
                incentivo.text = when {
                    pagina.ml >= meta -> "Meta alcançada. Um cuidado para celebrar!"
                    pagina.ml == 0 && pagina.tipo == "Água" -> "Seu próximo gole pode ser agora."
                    pagina.tipo == "Água" || pagina.tipo == null -> "Cada gole conta. Você está no seu ritmo."
                    else -> "${pagina.titulo} de hoje. Veja também seu total diário."
                }
                animarProgressBar(pagina.ml.coerceAtMost(meta))
            }
        }
        findViewById<View>(R.id.navegacaoBebidas).visibility = if (paginasConsumo.size>1) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.tvPaginaBebida).text = "${indice+1} de ${paginasConsumo.size} · Deslize para os lados"
        findViewById<View>(R.id.btnBebidaAnterior).apply {
            isEnabled = indice>0;alpha=if (isEnabled) 1f else .3f
            contentDescription = if (isEnabled) "Ver ${paginasConsumo[indice-1].titulo}" else "Primeira bebida"
        }
        findViewById<View>(R.id.btnProximaBebida).apply {
            isEnabled = indice<paginasConsumo.lastIndex;alpha=if (isEnabled) 1f else .3f
            contentDescription = if (isEnabled) "Ver ${paginasConsumo[indice+1].titulo}" else "Última página"
        }
        if (direcao != 0) findViewById<View>(R.id.infoConsumo).apply {
            animate().cancel();alpha=.35f;translationX=direcao*16*resources.displayMetrics.density
            animate().alpha(1f).translationX(0f).setDuration(200).start()
        }
    }

    private fun animarProgressBar(targetProgress: Int) {
        animacaoProgresso = ObjectAnimator.ofInt(pbMeta, "progress", pbMeta.progress, targetProgress).apply {
            duration = 750
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    override fun onDestroy() {
        animacaoProgresso?.cancel()
        super.onDestroy()
    }
}

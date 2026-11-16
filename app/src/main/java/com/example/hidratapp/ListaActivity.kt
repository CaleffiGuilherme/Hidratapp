package com.example.hidratapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaActivity : DadosActivity() {
    private lateinit var periodo: Spinner
    private lateinit var tipo: Spinner
    private lateinit var favoritos: CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista)
        aplicarVisual()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, i ->
            val b = i.getInsets(WindowInsetsCompat.Type.systemBars()); val margem = (16 * resources.displayMetrics.density).toInt(); v.setPadding(b.left + margem, b.top, b.right + margem, b.bottom); i
        }
        findViewById<RecyclerView>(R.id.rvBebidas).layoutManager = LinearLayoutManager(this)
        findViewById<ImageButton>(R.id.btnHeaderVoltar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }
        periodo = findViewById(R.id.filtroPeriodo)
        tipo = findViewById(R.id.filtroTipo)
        favoritos = findViewById(R.id.filtroFavoritos)
        periodo.adapter = ArrayAdapter(this, R.layout.item_filtro_spinner, listOf(getString(R.string.lista_todo_o_historico), getString(R.string.main_hoje), getString(R.string.lista_ultimos_7_dias)))
        tipo.adapter = ArrayAdapter(this, R.layout.item_filtro_spinner, listOf(getString(R.string.lista_todas_as_bebidas)) + TipoBebida.entries.map { it.nome(this) })
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { atualizar() }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        periodo.onItemSelectedListener = listener
        tipo.onItemSelectedListener = listener
        favoritos.setOnCheckedChangeListener { _, _ -> atualizar() }
    }
    override fun onResume() { super.onResume(); atualizar() }
    private fun atualizar() {
        dados({ listar() }) { todas ->
        val datas = Calculos.datasSemana()
        val totais = datas.map { dia -> Calculos.totalValido(todas.filter { it.data == dia }) }
        val meta = PerfilRepository.meta(this)
        findViewById<GraficoSemanal>(R.id.graficoSemanal).definir(datas, totais, meta)
        findViewById<TextView>(R.id.tvMediaSemanal).text = getString(R.string.volume_dia, totais.sum() / 7)
        findViewById<TextView>(R.id.resumoSemana).text =
            getString(R.string.lista_media_dos_ultimos_sete_dias) +
            (if (meta > 0) getString(R.string.lista_de_7_dias_atingiram_a_meta_atual, totais.count { it >= meta }, meta) else getString(R.string.lista_configure_seu_perfil_para_acompanhar_metas)) +
            getString(R.string.lista_a_linha_indica_sua_meta_atual_dias)
        val filtradas = todas.filter {
            (when (periodo.selectedItemPosition) { 1 -> it.data == Calculos.hoje(); 2 -> it.data in datas; else -> true }) &&
                (tipo.selectedItemPosition <= 0 || it.tipo == TipoBebida.entries[tipo.selectedItemPosition - 1].valorBanco) &&
                (!favoritos.isChecked || it.favorito)
        }
        findViewById<TextView>(R.id.tvEmptyState).apply {
            visibility = if (filtradas.isEmpty()) View.VISIBLE else View.GONE
            text = getString(R.string.lista_nenhum_registro_para_os_filtros_selecionados)
        }
        findViewById<RecyclerView>(R.id.rvBebidas).apply {
            visibility = if (filtradas.isEmpty()) View.GONE else View.VISIBLE
            adapter = BebidaAdapter(filtradas) { startActivity(Intent(this@ListaActivity, DetalheBebidaActivity::class.java).putExtra("bebida_id", it.id)) }
        }
        }
    }
}

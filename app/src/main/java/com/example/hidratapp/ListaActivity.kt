package com.example.hidratapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaActivity : AppCompatActivity() {
    private lateinit var periodo: Spinner
    private lateinit var tipo: Spinner
    private lateinit var favoritos: CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista)
        aplicarVisual()
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
        periodo.adapter = ArrayAdapter(this, R.layout.item_filtro_spinner, listOf("Todo o histórico", "Hoje", "Últimos 7 dias"))
        tipo.adapter = ArrayAdapter(this, R.layout.item_filtro_spinner, listOf("Todas as bebidas") + TIPOS)
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
        val todas = runCatching { BancoHelper(this).use { it.listar() } }.getOrElse {
            Toast.makeText(this, "Não foi possível carregar o histórico", Toast.LENGTH_LONG).show(); return
        }
        val datas = Calculos.datasSemana()
        val totais = datas.map { dia -> Calculos.totalValido(todas.filter { it.data == dia }) }
        val meta = PerfilRepository.meta(this)
        findViewById<GraficoSemanal>(R.id.graficoSemanal).definir(datas, totais, meta)
        findViewById<TextView>(R.id.tvMediaSemanal).text = "${totais.sum() / 7} ml / dia"
        findViewById<TextView>(R.id.resumoSemana).text =
            "Média dos últimos sete dias\n" +
            (if (meta > 0) "${totais.count { it >= meta }} de 7 dias atingiram a meta atual de $meta ml" else "Configure seu perfil para acompanhar metas") +
            "\nA linha indica sua meta atual · Dias sem registros = 0"
        val filtradas = todas.filter {
            (when (periodo.selectedItemPosition) { 1 -> it.data == Calculos.hoje(); 2 -> it.data in datas; else -> true }) &&
                (tipo.selectedItemPosition <= 0 || it.tipo == TIPOS[tipo.selectedItemPosition - 1]) &&
                (!favoritos.isChecked || it.favorito)
        }
        findViewById<TextView>(R.id.tvEmptyState).apply {
            visibility = if (filtradas.isEmpty()) View.VISIBLE else View.GONE
            text = "Nenhum registro para os filtros selecionados."
        }
        findViewById<RecyclerView>(R.id.rvBebidas).apply {
            visibility = if (filtradas.isEmpty()) View.GONE else View.VISIBLE
            adapter = BebidaAdapter(filtradas) { startActivity(Intent(this@ListaActivity, DetalheBebidaActivity::class.java).putExtra("bebida_id", it.id)) }
        }
    }
}

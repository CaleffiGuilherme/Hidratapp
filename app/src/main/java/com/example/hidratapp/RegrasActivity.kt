package com.example.hidratapp

import android.os.Bundle
import android.text.util.Linkify
import androidx.appcompat.app.AppCompatActivity

class RegrasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val box=paginaSimples()
        cabecalhoPagina(box,getString(R.string.regras_informacao_tambem_e_cuidado),getString(R.string.regras_no_seu_ritmo_com_clareza),getString(R.string.regras_entenda_o_que_sua_meta_representa_e))
        val secoes=listOf(
            getString(R.string.regras_uma_estimativa_nao_uma_prescricao) to getString(R.string.regras_a_meta_e_uma_estimativa_do_projeto),
            getString(R.string.regras_como_calculamos_sua_meta) to getString(R.string.regras_peso_35_ml_kg_abaixo_de_18),
            getString(R.string.regras_o_que_entra_no_seu_progresso) to getString(R.string.regras_somamos_o_volume_de_todas_as_bebidas),
            getString(R.string.regras_sua_semana_em_perspectiva) to getString(R.string.regras_o_grafico_inclui_os_ultimos_sete_dias),
            getString(R.string.regras_seus_dados_perto_de_voce) to getString(R.string.regras_os_dados_ficam_no_aparelho_sujeitos_ao)
        )
        secoes.forEach { (titulo,descricao) -> secaoPagina(box,titulo).addView(textoPagina(descricao,14f,true)) }
        val fontes=secaoPagina(box,getString(R.string.regras_para_continuar_aprendendo))
        fontes.addView(textoPagina(getString(R.string.regras_referencias_educativas_elas_nao_validam_os_coeficientes),12f,true))
        listOf(
            getString(R.string.regras_cdc_agua_e_bebidas_https_www_cdc),
            getString(R.string.regras_nhs_liquidos_e_hidratacao_https_www_nhs),
            getString(R.string.regras_medlineplus_agua_na_alimentacao_https_medlineplus_gov)
        ).forEach { referencia -> fontes.addView(textoPagina(referencia,13f).apply { Linkify.addLinks(this,Linkify.WEB_URLS);movementMethod=android.text.method.LinkMovementMethod.getInstance() }) }
        aplicarVisual()
    }
}

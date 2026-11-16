package com.example.hidratapp

import android.content.Context

enum class NivelAlerta { INFO, ALERTA, PERIGO }
data class Dica(val titulo: String, val descricao: String, val nivelAlerta: NivelAlerta)

object DicasEngine {
    fun gerarDicas(context: Context, bebidasHoje: List<Bebida>, sintomasHoje: List<Sintoma>): List<Dica> {
        val dicas = mutableListOf<Dica>()
        val prefs = context.getSharedPreferences("perfil", Context.MODE_PRIVATE)
        val alcool = bebidasHoje.filter { Calculos.ehAlcool(it.tipo) }
        if (alcool.isNotEmpty()) dicas.add(Dica(context.getString(R.string.dica_consumo_de_alcool),
            context.getString(R.string.dica_voce_registrou_bebida_s_alcoolica_s_agua, alcool.size), NivelAlerta.ALERTA))
        if (bebidasHoje.any { TipoBebida.doBanco(it.tipo)?.cafeinada == true }) dicas.add(Dica(context.getString(R.string.dica_bebidas_com_cafeina),
            context.getString(R.string.dica_essas_bebidas_tambem_contribuem_com_liquidos_observe), NivelAlerta.INFO))
        if (bebidasHoje.any { it.tipo == context.getString(R.string.dica_suco) || it.tipo == context.getString(R.string.dica_refrigerante) }) dicas.add(Dica(context.getString(R.string.dica_confira_o_acucar),
            if (prefs.getBoolean("diabetes", false)) context.getString(R.string.dica_bebidas_podem_conter_acucar_siga_seu_plano)
            else context.getString(R.string.dica_alguns_sucos_e_refrigerantes_contem_bastante_acucar), NivelAlerta.INFO))
        if (prefs.getBoolean("pedras_rins", false) || prefs.getBoolean("hipertensao", false)) dicas.add(Dica(context.getString(R.string.dica_sua_orientacao_de_saude_vem_primeiro),
            context.getString(R.string.dica_as_necessidades_de_liquidos_variam_conforme_sua), NivelAlerta.INFO))
        if (sintomasHoje.isNotEmpty()) dicas.add(Dica(context.getString(R.string.dica_sintomas_registrados),
            context.getString(R.string.dica_voce_relatou_sintomas_podem_ter_varias_causas, sintomasHoje.joinToString { it.sintomasText }), NivelAlerta.ALERTA))
        val meta = PerfilRepository.meta(context)
        val total = Calculos.totalValido(bebidasHoje)
        if (meta <= 0) dicas.add(Dica(context.getString(R.string.dica_configure_seu_perfil), context.getString(R.string.dica_preencha_seu_perfil_para_acompanhar_uma_meta), NivelAlerta.INFO))
        else {
            val pct = total.toLong() * 100 / meta
            dicas.add(Dica(if (total >= meta) context.getString(R.string.dica_meta_alcancada) else context.getString(R.string.dica_seu_progresso_de_hoje),
                "$total / $meta ml ($pct%). " + if (total >= meta) context.getString(R.string.dica_nao_e_necessario_continuar_bebendo_para_aumentar) else context.getString(R.string.dica_distribua_seu_consumo_ao_longo_do_dia), NivelAlerta.INFO))
        }
        dicas.add(Dica(context.getString(R.string.dica_informacoes_educativas), context.getString(R.string.dica_a_meta_e_uma_estimativa_do_projeto), NivelAlerta.INFO))
        return dicas
    }
}

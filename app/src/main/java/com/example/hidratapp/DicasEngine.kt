package com.example.hidratapp

import android.content.Context

enum class NivelAlerta { INFO, ALERTA, PERIGO }
data class Dica(val titulo: String, val descricao: String, val nivelAlerta: NivelAlerta)

object DicasEngine {
    fun gerarDicas(context: Context, bebidasHoje: List<Bebida>, sintomasHoje: List<Sintoma>): List<Dica> {
        val dicas = mutableListOf<Dica>()
        val prefs = context.getSharedPreferences("perfil", Context.MODE_PRIVATE)
        val alcool = bebidasHoje.filter { Calculos.ehAlcool(it.tipo) }
        if (alcool.isNotEmpty()) dicas.add(Dica("Consumo de álcool",
            "Você registrou ${alcool.size} bebida(s) alcoólica(s). Água não neutraliza o álcool nem torna o consumo seguro. Evite dirigir após beber. Se há histórico de problemas com álcool, busque apoio profissional.", NivelAlerta.ALERTA))
        if (bebidasHoje.any { it.tipo == "Café" || it.tipo == "Energético" }) dicas.add(Dica("Bebidas com cafeína",
            "Essas bebidas também contribuem com líquidos. Observe a quantidade de cafeína e açúcar no rótulo e priorize água no cotidiano.", NivelAlerta.INFO))
        if (bebidasHoje.any { it.tipo == "Suco" || it.tipo == "Refrigerante" }) dicas.add(Dica("Confira o açúcar",
            if (prefs.getBoolean("diabetes", false)) "Bebidas podem conter açúcar. Siga seu plano de cuidados e a orientação do profissional que acompanha sua glicemia."
            else "Alguns sucos e refrigerantes contêm bastante açúcar. Confira o rótulo e priorize água.", NivelAlerta.INFO))
        if (prefs.getBoolean("pedras_rins", false) || prefs.getBoolean("hipertensao", false)) dicas.add(Dica("Sua orientação de saúde vem primeiro",
            "As necessidades de líquidos variam conforme sua saúde e tratamento. Combine a meta com um profissional e respeite eventuais restrições de líquidos.", NivelAlerta.INFO))
        if (sintomasHoje.isNotEmpty()) dicas.add(Dica("Sintomas registrados",
            "Você relatou: ${sintomasHoje.joinToString { it.sintomasText }}. Sintomas podem ter várias causas; o aplicativo não faz diagnóstico. Se forem intensos, persistirem ou piorarem, procure atendimento de saúde.", NivelAlerta.ALERTA))
        val meta = PerfilRepository.meta(context)
        val total = Calculos.totalValido(bebidasHoje)
        if (meta <= 0) dicas.add(Dica("Configure seu perfil", "Preencha seu perfil para acompanhar uma meta estimada de consumo.", NivelAlerta.INFO))
        else {
            val pct = total.toLong() * 100 / meta
            dicas.add(Dica(if (total >= meta) "Meta alcançada!" else "Seu progresso de hoje",
                "$total / $meta ml ($pct%). " + if (total >= meta) "Não é necessário continuar bebendo para aumentar o percentual." else "Distribua seu consumo ao longo do dia, respeitando suas necessidades e orientações de saúde.", NivelAlerta.INFO))
        }
        dicas.add(Dica("Informações educativas", "A meta é uma estimativa do projeto. Consulte Regras, limites e fontes na tela Sobre para entender o cálculo e suas limitações.", NivelAlerta.INFO))
        return dicas
    }
}

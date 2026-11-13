package com.example.hidratapp

import android.content.Context

enum class NivelAlerta {
    INFO,
    ALERTA,
    PERIGO
}

data class Dica(
    val titulo: String,
    val descricao: String,
    val nivelAlerta: NivelAlerta
)

object DicasEngine {

    fun gerarDicas(context: Context, bebidasHoje: List<Bebida>, sintomasHoje: List<Sintoma>): List<Dica> {
        val dicas = mutableListOf<Dica>()

        val prefs = context.getSharedPreferences("perfil", Context.MODE_PRIVATE)
        val temDiabetes = prefs.getBoolean("diabetes", false)
        val temAlcoolismo = prefs.getBoolean("alcoolismo", false)
        val temPedrasRins = prefs.getBoolean("pedras_rins", false)

        val totalMlAgua = bebidasHoje.filter { !Calculos.ehAlcool(it.tipo) }.sumOf { it.ml }
        val bebidasAlcool = bebidasHoje.filter { Calculos.ehAlcool(it.tipo) }
        val bebidasCafeina = bebidasHoje.filter { it.tipo == "Café" || it.tipo == "Energético" }
        val bebidasAcucar = bebidasHoje.filter { it.tipo == "Refrigerante" || it.tipo == "Suco" }

        // 1. Alcoolismo & Consumo de Álcool
        if (temAlcoolismo) {
            if (bebidasAlcool.isNotEmpty()) {
                dicas.add(
                    Dica(
                        titulo = "Atenção Especial: Consumo de Álcool",
                        descricao = "Você cadastrou histórico com álcool no perfil e registrou bebidas alcoólicas hoje. Lembre-se de priorizar sua saúde, interromper o consumo e ingerir água pura.",
                        nivelAlerta = NivelAlerta.PERIGO
                    )
                )
            } else {
                dicas.add(
                    Dica(
                        titulo = "Excelente escolha!",
                        descricao = "Mantenha o hábito saudável longe de bebidas alcoólicas. Seu organismo e sua saúde agradecem!",
                        nivelAlerta = NivelAlerta.INFO
                    )
                )
            }
        } else if (bebidasAlcool.isNotEmpty()) {
            val qtd = bebidasAlcool.size
            dicas.add(
                Dica(
                    titulo = "Hidratação Compensatória",
                    descricao = "Você registrou $qtd bebida(s) alcoólica(s) hoje. O álcool inibe o hormônio vasopressina (ADH) e acelera a perda de água. Beba 1 copo (300ml) de água para cada dose de álcool.",
                    nivelAlerta = NivelAlerta.ALERTA
                )
            )
        }

        // 2. Cafeína
        if (bebidasCafeina.isNotEmpty()) {
            val totalMlCafeina = bebidasCafeina.sumOf { it.ml }
            dicas.add(
                Dica(
                    titulo = "Atenção ao Efeito Diurético da Cafeína",
                    descricao = "Você consumiu $totalMlCafeina ml de bebidas com cafeína (café/energético). A cafeína estimula os rins a expelir mais água. Beba água adicional para neutralizar a perda.",
                    nivelAlerta = NivelAlerta.ALERTA
                )
            )
        }

        // 3. Açúcar & Diabetes
        if (bebidasAcucar.isNotEmpty()) {
            if (temDiabetes) {
                dicas.add(
                    Dica(
                        titulo = "Alerta de Glicemia (Diabetes/Pré-diabetes)",
                        descricao = "Bebidas açucaradas ou sucos industrializados podem elevar rapidamente a glicose no sangue. Como você tem restrição glicêmica, priorize água pura ou chás sem açúcar.",
                        nivelAlerta = NivelAlerta.PERIGO
                    )
                )
            } else if (bebidasAcucar.sumOf { it.ml } >= 500) {
                dicas.add(
                    Dica(
                        titulo = "Modere Refrigerantes e Sucos Açucarados",
                        descricao = "Bebidas com alto teor de açúcar e sódio não substituem a água e podem causar sede rebote. Substitua por água aromatizada ou sucos naturais sem açúcar.",
                        nivelAlerta = NivelAlerta.ALERTA
                    )
                )
            }
        }

        // 4. Pedras nos rins / Saúde Renal
        if (temPedrasRins) {
            dicas.add(
                Dica(
                    titulo = "Saúde Renal & Cálculo Renal",
                    descricao = "Quem possui tendência a pedras nos rins deve beber água de forma fracionada durante todo o dia (1 copo a cada 1 ou 2 horas), mantendo a urina sempre clara e transparente.",
                    nivelAlerta = NivelAlerta.INFO
                )
            )
        }

        // 5. Sintomas Relatados
        if (sintomasHoje.isNotEmpty()) {
            val textosSintomas = sintomasHoje.joinToString { it.sintomasText }
            dicas.add(
                Dica(
                    titulo = "Relato de Sintomas de Hoje",
                    descricao = "Você relatou: $textosSintomas. Dor de cabeça, tontura e boca seca são sinais diretos de desidratação. Procure tomar 300ml a 500ml de água e repousar.",
                    nivelAlerta = NivelAlerta.ALERTA
                )
            )
        }

        // 6. Meta Geral de Hidratação
        if (prefs.contains("peso")) {
            val peso = prefs.getFloat("peso", 0f).toDouble()
            val idade = prefs.getInt("idade", 0)
            val masculino = prefs.getBoolean("masculino", false)
            val meta = Calculos.metaAgua(peso, idade, masculino)

            if (meta > 0) {
                val pct = (totalMlAgua * 100) / meta
                if (pct >= 100) {
                    dicas.add(
                        Dica(
                            titulo = "Meta Alcançada! 🎉",
                            descricao = "Parabéns! Você alcançou $pct% da sua meta diária de hidratação ($totalMlAgua / $meta ml). Seu corpo funciona no seu potencial máximo!",
                            nivelAlerta = NivelAlerta.INFO
                        )
                    )
                } else if (pct < 50) {
                    dicas.add(
                        Dica(
                            titulo = "Aumente seu consumo de água",
                            descricao = "Você consumiu apenas $pct% da sua meta diária de hoje ($totalMlAgua / $meta ml). Tente beber 1 copo de água agora para se manter bem hidratado.",
                            nivelAlerta = NivelAlerta.ALERTA
                        )
                    )
                }
            }
        } else {
            dicas.add(
                Dica(
                    titulo = "Configure seu Perfil",
                    descricao = "Preencha suas informações pessoais no seu perfil para receber recomendações de meta diária de água sob medida.",
                    nivelAlerta = NivelAlerta.INFO
                )
            )
        }

        // Dica Padrão de Bem-estar caso haja poucas dicas
        if (dicas.isEmpty()) {
            dicas.add(
                Dica(
                    titulo = "Dica Diária de Saúde",
                    descricao = "Beber água melhora a concentração, a pele e o funcionamento dos rins. Mantenha uma garrafa por perto durante suas atividades!",
                    nivelAlerta = NivelAlerta.INFO
                )
            )
        }

        return dicas
    }
}

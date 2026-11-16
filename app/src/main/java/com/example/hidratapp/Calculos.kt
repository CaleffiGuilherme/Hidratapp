package com.example.hidratapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Calculos {

    // Coeficientes em AjustesDoApp; faixas de idade e ordem do cálculo neste método.
    fun metaAgua(peso: Double, idade: Int, masculino: Boolean, nivelEsporte: Int = 0): Int {
        var mlPorKg = AjustesDoApp.META_BASE_ML_POR_KG
        if (idade < 18) {
            mlPorKg = AjustesDoApp.META_JOVEM_ML_POR_KG
        } else if (idade > 55) {
            mlPorKg = AjustesDoApp.META_MAIS_55_ML_POR_KG
        }

        // Adiciona ml/kg de acordo com o nível de esporte
        mlPorKg += when (nivelEsporte) {
            1 -> AjustesDoApp.ADICIONAL_ATIVIDADE_LEVE   // Leve (1-2x semana)
            2 -> AjustesDoApp.ADICIONAL_ATIVIDADE_MODERADA  // Moderado (3-4x semana)
            3 -> AjustesDoApp.ADICIONAL_ATIVIDADE_INTENSA  // Intenso (5+x semana / Atleta)
            else -> 0 // Sedentário
        }

        var meta = peso * mlPorKg
        if (masculino) {
            meta *= AjustesDoApp.FATOR_MASCULINO
        }
        return meta.toInt()
    }

    fun imc(peso: Double, alturaCm: Int): Double {
        val metros = alturaCm / 100.0
        return peso / (metros * metros)
    }

    fun horaAgora(): String = SimpleDateFormat("HH:mm", Locale.ROOT).format(Date(RelogioDoApp.agora()))

    fun totalValido(lista: List<Bebida>): Int = lista.filter { !ehAlcool(it.tipo) }.sumOf { it.ml }

    fun datasSemana(): List<String> {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -6)
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        return (0..6).map { formato.format(cal.time).also { cal.add(java.util.Calendar.DAY_OF_YEAR, 1) } }
    }

    fun hoje(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(Date(RelogioDoApp.agora()))
    }

    fun ehAlcool(tipo: String): Boolean {
        return TipoBebida.doBanco(tipo)?.alcoolica == true
    }

    fun alcoolSeguidos(lista: List<Bebida>): Int {
        var contador = 0
        for (bebida in lista) {
            if (ehAlcool(bebida.tipo)) {
                contador++
            } else {
                break
            }
        }
        return contador
    }
}

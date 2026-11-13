package com.example.hidratapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Calculos {

    fun metaAgua(peso: Double, idade: Int, masculino: Boolean, nivelEsporte: Int = 0): Int {
        var mlPorKg = 35
        if (idade < 18) {
            mlPorKg = 40
        } else if (idade > 55) {
            mlPorKg = 30
        }

        // Adiciona ml/kg de acordo com o nível de esporte
        mlPorKg += when (nivelEsporte) {
            1 -> 5   // Leve (1-2x semana)
            2 -> 10  // Moderado (3-4x semana)
            3 -> 15  // Intenso (5+x semana / Atleta)
            else -> 0 // Sedentário
        }

        var meta = peso * mlPorKg
        if (masculino) {
            meta *= 1.05
        }
        return meta.toInt()
    }

    fun imc(peso: Double, alturaCm: Int): Double {
        val metros = alturaCm / 100.0
        return peso / (metros * metros)
    }

    fun hoje(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    fun ehAlcool(tipo: String): Boolean {
        return TIPOS_ALCOOL.contains(tipo)
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

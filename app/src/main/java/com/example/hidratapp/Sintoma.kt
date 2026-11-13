package com.example.hidratapp

data class Sintoma(
    val id: Int = 0,
    val data: String,
    val sintomasText: String,
    val nivelSede: Int,
    val observacao: String
)

val SINTOMAS_DISPONIVEIS = listOf(
    "Dor de cabeça",
    "Boca seca / Lábios rachados",
    "Tontura / Tonturas ao levantar",
    "Fadiga / Cansaço excessivo",
    "Urina escura / Concentrada",
    "Cãibra muscular",
    "Nausea / Enjoo"
)

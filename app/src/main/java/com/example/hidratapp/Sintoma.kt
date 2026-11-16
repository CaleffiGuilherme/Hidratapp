package com.example.hidratapp

data class Sintoma(
    val id: Int = 0,
    val data: String,
    val sintomasText: String,
    val nivelSede: Int,
    val observacao: String
)

val SINTOMAS_DISPONIVEIS = listOf(
    R.string.sintoma_opcao_0,
    R.string.sintoma_opcao_1,
    R.string.sintoma_opcao_2,
    R.string.sintoma_opcao_3,
    R.string.sintoma_opcao_4,
    R.string.sintoma_opcao_5,
    R.string.sintoma_opcao_6
)

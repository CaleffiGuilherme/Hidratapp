package com.example.hidratapp

data class Bebida(
    val id: Int = 0,
    val tipo: String,
    val ml: Int,
    val data: String,
    val observacao: String,
    val favorito: Boolean = false
)

val TIPOS = listOf(
    "Água", "Suco", "Refrigerante", "Café",
    "Energético", "Cerveja", "Vinho", "Destilado"
)

val TIPOS_ALCOOL = listOf("Cerveja", "Vinho", "Destilado")

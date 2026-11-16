package com.example.hidratapp

import org.junit.Assert.*
import org.junit.Test

class TipoBebidaTest {
    @Test fun reconheceValoresJaGravadosNoBanco() {
        val antigos = listOf("Água", "Suco", "Refrigerante", "Café", "Energético", "Cerveja", "Vinho", "Destilado")
        assertEquals(antigos, TipoBebida.entries.map { it.valorBanco })
        antigos.forEach { assertNotNull(TipoBebida.doBanco(it)) }
        assertEquals(TipoBebida.entries.size, TipoBebida.entries.map { it.chipId }.distinct().size)
    }

    @Test fun classificacoesPreservamRegrasDeConsumo() {
        assertEquals(setOf("Cerveja", "Vinho", "Destilado"),
            TipoBebida.entries.filter { it.alcoolica }.map { it.valorBanco }.toSet())
        assertEquals(setOf("Café", "Energético"),
            TipoBebida.entries.filter { it.cafeinada }.map { it.valorBanco }.toSet())
    }

    @Test fun categoriaDesconhecidaContinuaLegivel() {
        assertNull(TipoBebida.doBanco("Chá"))
        assertEquals("Chá", TipoBebida.nomeExibido("Chá"))
        assertFalse(Calculos.ehAlcool("Chá"))
    }
}

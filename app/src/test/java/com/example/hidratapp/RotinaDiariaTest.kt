package com.example.hidratapp

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class RotinaDiariaTest {
    private fun instante(texto: String, fuso: TimeZone) = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).apply {
        timeZone = fuso
    }.parse(texto)!!.time

    @Test fun viradaAvancaAnoEMes() {
        val fuso = TimeZone.getTimeZone("America/Sao_Paulo")
        assertEquals(instante("2027-01-01 00:00",fuso),
            RotinaDiaria.proximaMeiaNoite(instante("2026-12-31 23:59",fuso),fuso))
    }
    @Test fun calculaProximoDiaMesmoEmHorarioDeVerao() {
        val fuso = TimeZone.getTimeZone("America/New_York")
        val inicio = instante("2026-03-08 00:00",fuso)
        assertEquals(23*60*60*1000L,RotinaDiaria.proximaMeiaNoite(inicio,fuso)-inicio)
    }
    @Test fun celebracaoOcorreUmaVezPorDiaComMetaValida() {
        assertTrue(RotinaDiaria.celebrar("16/11/2026","15/11/2026",2500,2500))
        assertFalse(RotinaDiaria.celebrar("16/11/2026","16/11/2026",3000,2500))
        assertFalse(RotinaDiaria.celebrar("16/11/2026",null,2400,2500))
        assertFalse(RotinaDiaria.celebrar("16/11/2026",null,3000,0))
    }
}

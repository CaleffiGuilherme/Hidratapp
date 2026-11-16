package com.example.hidratapp

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class CalculosTest {
    @Test fun metaConsideraIdadeSexoEAtividade() {
        assertEquals(2450, Calculos.metaAgua(70.0, 30, false))
        assertEquals(2572, Calculos.metaAgua(70.0, 30, true))
        assertEquals(2800, Calculos.metaAgua(70.0, 17, false))
        assertEquals(2450, Calculos.metaAgua(70.0, 18, false))
        assertEquals(2450, Calculos.metaAgua(70.0, 55, false))
        assertEquals(2100, Calculos.metaAgua(70.0, 56, false))
        assertEquals(2800, Calculos.metaAgua(70.0, 30, false, 1))
        assertEquals(3150, Calculos.metaAgua(70.0, 30, false, 2))
        assertEquals(3500, Calculos.metaAgua(70.0, 30, false, 3))
    }
    @Test fun imcConverteCentimetrosParaMetros() { assertEquals(22.857, Calculos.imc(70.0, 175), .001) }
    private fun bebida(tipo: String, ml: Int = 300) = Bebida(tipo=tipo, ml=ml, data="15/11/2026", observacao="")
    @Test fun progressoSomaBebidasSemAlcool() {
        assertEquals(0, Calculos.totalValido(emptyList()))
        assertEquals(600, Calculos.totalValido(listOf(bebida("Água"), bebida("Café"), bebida("Cerveja"))))
        assertEquals(0, Calculos.totalValido(TIPOS_ALCOOL.map { bebida(it) }))
    }
    @Test fun alertaConsideraApenasSequenciaMaisRecente() {
        assertEquals(0, Calculos.alcoolSeguidos(emptyList()))
        assertEquals(2, Calculos.alcoolSeguidos(listOf(bebida("Vinho"), bebida("Cerveja"), bebida("Água"), bebida("Vinho"))))
        assertEquals(0, Calculos.alcoolSeguidos(listOf(bebida("Água"), bebida("Vinho"))))
    }
    private fun instante(dia: Int, h: Int, m: Int) = Calendar.getInstance().apply {
        set(2026, Calendar.NOVEMBER, dia, h, m, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    @Test fun lembreteAntesDoInicioVaiParaPrimeiroHorario() {
        assertEquals(instante(15,8,0), Lembretes.proximoHorario(instante(15,7,0),480,1320,60))
    }
    @Test fun lembreteAvancaPeloIntervaloSemRepetirHorarioAtual() {
        assertEquals(instante(15,9,0), Lembretes.proximoHorario(instante(15,8,0),480,1320,60))
        assertEquals(instante(15,9,30), Lembretes.proximoHorario(instante(15,8,5),480,1320,90))
    }
    @Test fun lembreteAposFimVaiParaDiaSeguinte() {
        assertEquals(instante(16,8,0), Lembretes.proximoHorario(instante(15,22,0),480,1320,60))
        assertEquals(instante(16,8,0), Lembretes.proximoHorario(instante(15,23,0),480,1320,60))
    }
    @Test(expected = IllegalArgumentException::class) fun rejeitaPeriodoInvertido() {
        Lembretes.proximoHorario(instante(15,8,0),1320,480,60)
    }
    @Test(expected = IllegalArgumentException::class) fun rejeitaIntervaloZero() {
        Lembretes.proximoHorario(instante(15,8,0),480,1320,0)
    }
    @Test fun semanaIncluiHojeESeteDiasDistintos() {
        val dias = Calculos.datasSemana()
        assertEquals(7, dias.size); assertEquals(7, dias.toSet().size); assertEquals(Calculos.hoje(), dias.last())
    }
}

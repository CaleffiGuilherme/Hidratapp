package com.example.hidratapp

import org.junit.Assert.*
import org.junit.Test

class ConsumoDoDiaTest {
    private fun bebida(tipo: String, ml: Int)=Bebida(tipo=tipo,ml=ml,data="16/11/2026",observacao="")
    @Test fun comecaEmAguaMesmoSemRegistro() {
        assertEquals(listOf(PaginaConsumo("Água",0)),ConsumoDoDia.paginas(emptyList()))
    }
    @Test fun agrupaRegistrosDaMesmaBebida() {
        val paginas=ConsumoDoDia.paginas(listOf(bebida("Café",100),bebida("Água",300),bebida("Café",200)))
        assertEquals(listOf(PaginaConsumo("Água",300),PaginaConsumo("Café",300),PaginaConsumo(null,600)),paginas)
    }
    @Test fun alcoolTemPaginaMasNaoSomaNaMeta() {
        val paginas=ConsumoDoDia.paginas(listOf(bebida("Cerveja",350)))
        assertEquals(listOf(PaginaConsumo("Água",0),PaginaConsumo("Cerveja",350),PaginaConsumo(null,0)),paginas)
    }
    @Test fun aguaSozinhaNaoDuplicaPaginaTotal() {
        assertEquals(listOf(PaginaConsumo("Água",500)),ConsumoDoDia.paginas(listOf(bebida("Água",200),bebida("Água",300))))
    }
    @Test fun ordemNaoMudaComNovoRegistroEIncluiTiposAntigos() {
        val paginas=ConsumoDoDia.paginas(listOf(bebida("Café",100),bebida("Chá",200),bebida("Suco",300)))
        assertEquals(listOf("Água","Suco","Café","Chá","Total do dia"),paginas.map { it.titulo })
    }
}

package com.example.hidratapp

import android.content.Context

/** Acesso aos registros. Nas telas, use dados({ operação }) { resultado } para executar em segundo plano. */
class BebidaRepository(context: Context, private val nomeBanco: String = "hidrata.db") {
    private val context = context.applicationContext
    private fun <T> banco(acao: BancoHelper.() -> T): T = BancoHelper(context, nomeBanco).use(acao)
    fun listar() = banco { listar() }
    fun listarPorData(data: String) = banco { listarPorData(data) }
    fun buscarPorId(id: Int) = banco { buscarPorId(id) }
    fun inserir(bebida: Bebida) = banco { inserir(bebida) }
    fun atualizar(bebida: Bebida) = banco { atualizar(bebida) }
    fun excluir(id: Int) = banco { excluir(id) }
    fun restaurar(bebida: Bebida) = banco { restaurar(bebida) }
    fun listarSintomas() = banco { listarSintomas() }
    fun listarSintomasPorData(data: String) = banco { listarSintomasPorData(data) }
    fun inserirSintoma(sintoma: Sintoma) = banco { inserirSintoma(sintoma) }
    fun excluirSintoma(id: Int) = banco { excluirSintoma(id) }

}

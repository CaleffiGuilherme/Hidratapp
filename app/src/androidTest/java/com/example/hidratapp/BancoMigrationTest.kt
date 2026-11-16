package com.example.hidratapp

import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BancoMigrationTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun migrar(versao: Int) {
        val nome = "migration_test_$versao.db"
        context.deleteDatabase(nome)
        try {
            context.openOrCreateDatabase(nome, 0, null).use { db ->
                db.execSQL("CREATE TABLE bebidas (id INTEGER PRIMARY KEY AUTOINCREMENT, tipo TEXT NOT NULL, ml INTEGER NOT NULL, data TEXT, observacao TEXT, favorito INTEGER DEFAULT 0)")
                db.execSQL("INSERT INTO bebidas(tipo,ml,data,observacao,favorito) VALUES ('Água',300,'14/11/2026','Anterior à migração',1)")
                if (versao == 2) {
                    db.execSQL("CREATE TABLE sintomas (id INTEGER PRIMARY KEY AUTOINCREMENT,data TEXT NOT NULL,sintomas TEXT NOT NULL,nivel_sede INTEGER NOT NULL,observacao TEXT)")
                    db.execSQL("INSERT INTO sintomas(data,sintomas,nivel_sede,observacao) VALUES ('14/11/2026','Boca seca',3,'Relato antigo')")
                }
                db.version = versao
            }
            BancoHelper(context, nome).use { banco ->
                val antiga = banco.listar().single()
                assertEquals("", antiga.hora); assertTrue(antiga.favorito); assertEquals(300, antiga.ml)
                if (versao == 2) assertEquals(1, banco.listarSintomasPorData("14/11/2026").size)
                else assertTrue(banco.listarSintomas().isEmpty())
                val id = banco.inserir(Bebida(tipo="Café", ml=200, data="15/11/2026", hora="09:30", observacao="Novo"))
                assertTrue(id > 0); assertEquals("09:30", banco.buscarPorId(id.toInt())!!.hora)
                assertEquals(id.toInt(), banco.listar().first().id)
                assertEquals(1, banco.atualizar(banco.buscarPorId(id.toInt())!!.copy(ml=500)))
                assertEquals(500, banco.buscarPorId(id.toInt())!!.ml)
                assertEquals(1, banco.excluir(id.toInt())); assertNull(banco.buscarPorId(id.toInt()))
            }
        } finally { context.deleteDatabase(nome) }
    }
    @Test fun migraV1SemPerderBebidas() = migrar(1)
    @Test fun migraV2SemPerderBebidasOuSintomas() = migrar(2)
    @Test fun ordenaPorConsumoEmVezDaOrdemDoCadastro() {
        val nome = "order_test.db"; context.deleteDatabase(nome)
        try { BancoHelper(context, nome).use { banco ->
            val maisNovo = banco.inserir(Bebida(tipo="Água",ml=200,data="01/01/2027",hora="15:00",observacao=""))
            banco.inserir(Bebida(tipo="Água",ml=200,data="31/12/2026",hora="23:00",observacao=""))
            banco.inserir(Bebida(tipo="Água",ml=200,data="01/01/2027",hora="09:00",observacao=""))
            assertEquals(maisNovo.toInt(), banco.listar().first().id)
            assertEquals(maisNovo.toInt(), banco.listarPorData("01/01/2027").first().id)
        } } finally { context.deleteDatabase(nome) }
    }
}

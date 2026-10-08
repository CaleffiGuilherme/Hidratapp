package com.example.hidratapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoHelper(context: Context) :
    SQLiteOpenHelper(context, "hidrata.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE bebidas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT NOT NULL,
                ml INTEGER NOT NULL,
                data TEXT,
                observacao TEXT,
                favorito INTEGER DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

    fun inserir(bebida: Bebida): Long {
        val valores = ContentValues().apply {
            put("tipo", bebida.tipo)
            put("ml", bebida.ml)
            put("data", bebida.data)
            put("observacao", bebida.observacao)
            put("favorito", if (bebida.favorito) 1 else 0)
        }
        return writableDatabase.insert("bebidas", null, valores)
    }

    private fun lerBebida(cursor: Cursor): Bebida {
        return Bebida(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
            ml = cursor.getInt(cursor.getColumnIndexOrThrow("ml")),
            data = cursor.getString(cursor.getColumnIndexOrThrow("data")),
            observacao = cursor.getString(cursor.getColumnIndexOrThrow("observacao")),
            favorito = cursor.getInt(cursor.getColumnIndexOrThrow("favorito")) == 1
        )
    }

    fun listar(): MutableList<Bebida> {
        val lista = mutableListOf<Bebida>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM bebidas ORDER BY id DESC", null
        )
        while (cursor.moveToNext()) {
            lista.add(lerBebida(cursor))
        }
        cursor.close()
        return lista
    }

    fun listarPorData(data: String): MutableList<Bebida> {
        val lista = mutableListOf<Bebida>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM bebidas WHERE data = ? ORDER BY id DESC",
            arrayOf(data)
        )
        while (cursor.moveToNext()) {
            lista.add(lerBebida(cursor))
        }
        cursor.close()
        return lista
    }

    fun buscarPorId(id: Int): Bebida? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM bebidas WHERE id = ?",
            arrayOf(id.toString())
        )
        val bebida = if (cursor.moveToFirst()) lerBebida(cursor) else null
        cursor.close()
        return bebida
    }

    fun atualizar(bebida: Bebida): Int {
        val valores = ContentValues().apply {
            put("tipo", bebida.tipo)
            put("ml", bebida.ml)
            put("data", bebida.data)
            put("observacao", bebida.observacao)
            put("favorito", if (bebida.favorito) 1 else 0)
        }
        return writableDatabase.update(
            "bebidas", valores, "id = ?", arrayOf(bebida.id.toString())
        )
    }

    fun excluir(id: Int): Int {
        return writableDatabase.delete(
            "bebidas", "id = ?", arrayOf(id.toString())
        )
    }
}

package com.example.hidratapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoHelper(context: Context, nomeBanco: String = "hidrata.db") :
    SQLiteOpenHelper(context, nomeBanco, null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS bebidas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT NOT NULL,
                ml INTEGER NOT NULL,
                data TEXT,
                observacao TEXT,
                hora TEXT NOT NULL DEFAULT '',
                favorito INTEGER DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS sintomas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                data TEXT NOT NULL,
                sintomas TEXT NOT NULL,
                nivel_sede INTEGER NOT NULL,
                observacao TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE bebidas ADD COLUMN hora TEXT NOT NULL DEFAULT ''")
        }
        if (oldVersion < 2) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS sintomas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data TEXT NOT NULL,
                    sintomas TEXT NOT NULL,
                    nivel_sede INTEGER NOT NULL,
                    observacao TEXT
                )
            """.trimIndent())
        }
    }

    fun inserir(bebida: Bebida): Long {
        val valores = ContentValues().apply {
            put("hora", bebida.hora)
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
            hora = cursor.getString(cursor.getColumnIndexOrThrow("hora")),
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
            "SELECT * FROM bebidas ORDER BY substr(data,7,4) DESC, substr(data,4,2) DESC, substr(data,1,2) DESC, hora DESC, id DESC", null
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
            "SELECT * FROM bebidas WHERE data = ? ORDER BY hora DESC, id DESC",
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
            put("hora", bebida.hora)
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

    // Métodos para Sintoma
    fun inserirSintoma(sintoma: Sintoma): Long {
        val valores = ContentValues().apply {
            put("data", sintoma.data)
            put("sintomas", sintoma.sintomasText)
            put("nivel_sede", sintoma.nivelSede)
            put("observacao", sintoma.observacao)
        }
        return writableDatabase.insert("sintomas", null, valores)
    }

    private fun lerSintoma(cursor: Cursor): Sintoma {
        return Sintoma(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            data = cursor.getString(cursor.getColumnIndexOrThrow("data")),
            sintomasText = cursor.getString(cursor.getColumnIndexOrThrow("sintomas")),
            nivelSede = cursor.getInt(cursor.getColumnIndexOrThrow("nivel_sede")),
            observacao = cursor.getString(cursor.getColumnIndexOrThrow("observacao"))
        )
    }

    fun listarSintomas(): MutableList<Sintoma> {
        val lista = mutableListOf<Sintoma>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM sintomas ORDER BY id DESC", null
        )
        while (cursor.moveToNext()) {
            lista.add(lerSintoma(cursor))
        }
        cursor.close()
        return lista
    }

    fun listarSintomasPorData(data: String): MutableList<Sintoma> {
        val lista = mutableListOf<Sintoma>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM sintomas WHERE data = ? ORDER BY id DESC",
            arrayOf(data)
        )
        while (cursor.moveToNext()) {
            lista.add(lerSintoma(cursor))
        }
        cursor.close()
        return lista
    }

    fun excluirSintoma(id: Int): Int {
        return writableDatabase.delete(
            "sintomas", "id = ?", arrayOf(id.toString())
        )
    }
}

package com.example.hidratapp

import android.content.Context

object PerfilRepository {
    fun meta(context: Context): Int {
        val p = context.getSharedPreferences("perfil", Context.MODE_PRIVATE)
        if (!p.contains("peso")) return 0
        return Calculos.metaAgua(p.getFloat("peso", 0f).toDouble(), p.getInt("idade", 0),
            p.getBoolean("masculino", false), p.getInt("esporte", 0))
    }
}

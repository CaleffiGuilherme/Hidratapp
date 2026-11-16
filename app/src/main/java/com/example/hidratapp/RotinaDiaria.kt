package com.example.hidratapp

import java.util.Calendar
import java.util.TimeZone

object RotinaDiaria {
    fun proximaMeiaNoite(agora: Long, fuso: TimeZone = TimeZone.getDefault()): Long =
        Calendar.getInstance(fuso).apply {
            timeInMillis = agora
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun celebrar(dia: String, ultimoDia: String?, total: Int, meta: Int): Boolean =
        meta > 0 && total >= meta && dia != ultimoDia
}

object RelogioDoApp {
    var agora: () -> Long = { System.currentTimeMillis() }
}

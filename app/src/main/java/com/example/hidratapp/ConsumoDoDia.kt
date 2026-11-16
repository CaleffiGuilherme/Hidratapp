package com.example.hidratapp

/** Pages show volumes of recorded drinks; the total keeps the existing hydration rule. */
data class PaginaConsumo(val tipo: String?, val ml: Int) {
    val chave: String get() = tipo ?: "__total__"
    val titulo: String get() = tipo ?: "Total do dia"
}

object ConsumoDoDia {
    fun paginas(registrosDoDia: List<Bebida>): List<PaginaConsumo> {
        val volumes = registrosDoDia.groupBy { it.tipo }.mapValues { (_, registros) -> registros.sumOf { it.ml } }
        val tipos = (listOf("Água") + TIPOS.filter { it != "Água" && volumes.containsKey(it) } +
            volumes.keys.filter { it !in TIPOS }).distinct()
        val paginas = tipos.map { PaginaConsumo(it, volumes[it] ?: 0) }
        return if (paginas.size > 1) paginas + PaginaConsumo(null, Calculos.totalValido(registrosDoDia)) else paginas
    }
}

package com.example.hidratapp

/** valorBanco is permanent: changing a label must not rename existing records. */
enum class TipoBebida(
    val valorBanco: String,
    val nome: String,
    val icone: Int,
    val chipId: Int,
    val alcoolica: Boolean = false,
    val cafeinada: Boolean = false
) {
    AGUA("Água", "Água", R.drawable.ic_water_drop, R.id.chipAgua),
    SUCO("Suco", "Suco", R.drawable.ic_drink_glass, R.id.chipSuco),
    REFRIGERANTE("Refrigerante", "Refrigerante", R.drawable.ic_drink_glass, R.id.chipRefrigerante),
    CAFE("Café", "Café", R.drawable.ic_coffee, R.id.chipCafe, cafeinada = true),
    ENERGETICO("Energético", "Energético", R.drawable.ic_drink_glass, R.id.chipEnergetico, cafeinada = true),
    CERVEJA("Cerveja", "Cerveja", R.drawable.ic_drink_glass, R.id.chipCerveja, alcoolica = true),
    VINHO("Vinho", "Vinho", R.drawable.ic_drink_glass, R.id.chipVinho, alcoolica = true),
    DESTILADO("Destilado", "Destilado", R.drawable.ic_drink_glass, R.id.chipDestilado, alcoolica = true);

    companion object {
        fun doBanco(valor: String): TipoBebida? = entries.firstOrNull { it.valorBanco == valor }
        fun nomeExibido(valor: String): String = doBanco(valor)?.nome ?: valor
    }
}

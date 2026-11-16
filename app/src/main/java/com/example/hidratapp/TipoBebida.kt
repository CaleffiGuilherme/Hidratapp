package com.example.hidratapp

/** CATÁLOGO DE BEBIDAS
 * Renomear: altere o texto tipo_* em strings.xml. Preserve valorBanco, usado no SQLite.
 * Adicionar: crie o texto em strings.xml, o ID em ids.xml e uma entrada neste enum.
 * Ícone e regras de álcool/cafeína ficam na própria entrada.
 */
enum class TipoBebida(
    val valorBanco: String,
    val nomeRes: Int,
    val icone: Int,
    val chipId: Int,
    val alcoolica: Boolean = false,
    val cafeinada: Boolean = false
) {
    AGUA("Água", R.string.tipo_agua, R.drawable.ic_water_drop, R.id.chipAgua),
    SUCO("Suco", R.string.tipo_suco, R.drawable.ic_drink_glass, R.id.chipSuco),
    REFRIGERANTE("Refrigerante", R.string.tipo_refrigerante, R.drawable.ic_drink_glass, R.id.chipRefrigerante),
    CAFE("Café", R.string.tipo_cafe, R.drawable.ic_coffee, R.id.chipCafe, cafeinada = true),
    ENERGETICO("Energético", R.string.tipo_energetico, R.drawable.ic_drink_glass, R.id.chipEnergetico, cafeinada = true),
    CERVEJA("Cerveja", R.string.tipo_cerveja, R.drawable.ic_drink_glass, R.id.chipCerveja, alcoolica = true),
    VINHO("Vinho", R.string.tipo_vinho, R.drawable.ic_drink_glass, R.id.chipVinho, alcoolica = true),
    DESTILADO("Destilado", R.string.tipo_destilado, R.drawable.ic_drink_glass, R.id.chipDestilado, alcoolica = true);

    fun nome(context: android.content.Context): String = context.getString(nomeRes)

    companion object {
        fun doBanco(valor: String): TipoBebida? = entries.firstOrNull { it.valorBanco == valor }
        fun nomeExibido(valor: String, context: android.content.Context): String = doBanco(valor)?.nome(context) ?: valor
    }
}

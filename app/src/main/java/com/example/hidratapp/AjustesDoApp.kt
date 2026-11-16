package com.example.hidratapp

/** Comece aqui para mudar valores na apresentação. Cores e textos ficam em res/values. */
object AjustesDoApp {
    // Volumes: os rótulos dos botões são atualizados automaticamente nas duas telas.
    const val ATALHO_PEQUENO_ML = 200
    const val ATALHO_MEDIO_ML = 300
    const val ATALHO_GRANDE_ML = 500
    const val VOLUME_MAXIMO_ML = 5000

    // Tempos em milissegundos: 1000 ms = 1 segundo.
    const val DESFAZER_REGISTRO_MS = 6000
    const val DESFAZER_EXCLUSAO_MS = 10000L
    const val ANIMACAO_PROGRESSO_MS = 750L
    const val ANIMACAO_COMEMORACAO_MS = 300L
    const val ANIMACAO_TROCA_MS = 200L

    // Coeficientes da estimativa acadêmica. A fórmula está em Calculos.metaAgua.
    const val META_BASE_ML_POR_KG = 35
    const val META_JOVEM_ML_POR_KG = 40
    const val META_MAIS_55_ML_POR_KG = 30
    const val ADICIONAL_ATIVIDADE_LEVE = 5
    const val ADICIONAL_ATIVIDADE_MODERADA = 10
    const val ADICIONAL_ATIVIDADE_INTENSA = 15
    const val FATOR_MASCULINO = 1.05
}

package com.example.hidratapp

import android.os.Bundle
import android.text.util.Linkify
import androidx.appcompat.app.AppCompatActivity

class RegrasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val box=paginaSimples()
        cabecalhoPagina(box,"INFORMAÇÃO TAMBÉM É CUIDADO","No seu ritmo, com clareza.","Entenda o que sua meta representa e como seus registros aparecem no aplicativo.")
        val secoes=listOf(
            "Uma estimativa, não uma prescrição" to "A meta é uma estimativa do projeto para organização de hábitos. As necessidades de líquidos variam; para menores de idade, condições de saúde ou restrição de líquidos, procure orientação profissional. Sintomas registrados não geram diagnóstico. Água não neutraliza o álcool.",
            "Como calculamos sua meta" to "Peso × 35 ml/kg. Abaixo de 18 anos: 40 ml/kg; acima de 55: 30 ml/kg. Atividade leve acrescenta 5 ml/kg; moderada, 10; intensa, 15. Para o sexo masculino, o resultado é multiplicado por 1,05 e arredondado para baixo em ml. Esses coeficientes são regras do projeto e não foram validados como recomendação médica individual.",
            "O que entra no seu progresso" to "Somamos o volume de todas as bebidas sem álcool. Água, café, suco, refrigerante e energético entram pelo volume registrado; isso não significa que tenham o mesmo valor nutricional. Álcool fica apenas no histórico. Alimentos não são contabilizados.",
            "Sua semana em perspectiva" to "O gráfico inclui os últimos sete dias, incluindo hoje. Dias sem registros contam como zero. A comparação usa sua meta atual; mudar o perfil recalcula essa comparação. Registros antigos não possuem horário. O IMC de menores de 20 anos não recebe classificação adulta.",
            "Seus dados, perto de você" to "Os dados ficam no aparelho, sujeitos ao backup configurado no Android. Não é necessário criar conta."
        )
        secoes.forEach { (titulo,descricao) -> secaoPagina(box,titulo).addView(textoPagina(descricao,14f,true)) }
        val fontes=secaoPagina(box,"Para continuar aprendendo")
        fontes.addView(textoPagina("Referências educativas. Elas não validam os coeficientes da fórmula do projeto.",12f,true))
        listOf(
            "CDC — Água e bebidas\nhttps://www.cdc.gov/healthy-weight-growth/water-healthy-drinks/index.html",
            "NHS — Líquidos e hidratação\nhttps://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/water-drinks-nutrition/",
            "MedlinePlus — Água na alimentação\nhttps://medlineplus.gov/ency/article/002471.htm"
        ).forEach { referencia -> fontes.addView(textoPagina(referencia,13f).apply { Linkify.addLinks(this,Linkify.WEB_URLS);movementMethod=android.text.method.LinkMovementMethod.getInstance() }) }
        aplicarVisual()
    }
}

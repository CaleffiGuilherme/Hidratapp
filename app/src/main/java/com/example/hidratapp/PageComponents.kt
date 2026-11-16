package com.example.hidratapp

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

fun AppCompatActivity.dp(value: Int) = (value * resources.displayMetrics.density).toInt()
fun AppCompatActivity.textoPagina(texto: String, tamanho: Float = 14f, secundario: Boolean = false): TextView = TextView(this).apply {
    text = texto; textSize = tamanho
    setTextColor(ContextCompat.getColor(this@textoPagina, if (secundario) R.color.text_secondary else R.color.text_primary))
    setLineSpacing(dp(3).toFloat(),1f)
    layoutParams = LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(10) }
}
fun AppCompatActivity.cabecalhoPagina(box: LinearLayout, categoria: String, titulo: String, resumo: String) {
    val row=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL; layoutParams=LinearLayout.LayoutParams(-1,dp(48)).apply { bottomMargin=dp(24) } }
    row.addView(ImageButton(this).apply {
        setImageResource(R.drawable.ic_arrow_back); setBackgroundResource(R.drawable.bg_white_round)
        setColorFilter(ContextCompat.getColor(this@cabecalhoPagina,R.color.blue_primary)); contentDescription=getString(R.string.acao_voltar)
        setPadding(dp(12),dp(12),dp(12),dp(12)); layoutParams=LinearLayout.LayoutParams(dp(48),dp(48));setOnClickListener { finish() }
    })
    row.addView(textoPagina(categoria,10f,true).apply { letterSpacing=.12f; layoutParams=LinearLayout.LayoutParams(0,-2,1f).apply { marginStart=dp(14) } })
    box.addView(row)
    box.addView(textoPagina(titulo,30f).apply { typeface=Typeface.create("sans-serif-medium",Typeface.NORMAL);letterSpacing=-.035f })
    box.addView(textoPagina(resumo,14f,true).apply { layoutParams=LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(24) } })
}
fun AppCompatActivity.secaoPagina(box: LinearLayout, titulo: String): LinearLayout {
    val card=MaterialCardView(this).apply { layoutParams=LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(16) } }
    val content=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL;setPadding(dp(20),dp(20),dp(20),dp(16)) }
    content.addView(textoPagina(titulo,16f).apply { typeface=Typeface.create("sans-serif-medium",Typeface.NORMAL) })
    card.addView(content);box.addView(card);return content
}
fun AppCompatActivity.botaoPagina(box: LinearLayout, texto: String, principal: Boolean = false, action: () -> Unit): MaterialButton = MaterialButton(this).apply {
    text=texto;isAllCaps=false;cornerRadius=dp(22);minHeight=dp(56)
    layoutParams=LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(6) }
    if (!principal) {
        backgroundTintList=ColorStateList.valueOf(ContextCompat.getColor(this@botaoPagina,R.color.blue_light))
        setTextColor(ContextCompat.getColor(this@botaoPagina,R.color.blue_primary))
    }
    setOnClickListener { action() };box.addView(this)
}

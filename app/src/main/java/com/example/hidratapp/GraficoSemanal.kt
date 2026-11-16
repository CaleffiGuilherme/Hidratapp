package com.example.hidratapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GraficoSemanal(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var dias = emptyList<String>()
    private var totais = emptyList<Int>()
    private var meta = 0
    fun definir(dias: List<String>, totais: List<Int>, meta: Int) {
        this.dias = dias; this.totais = totais; this.meta = meta
        contentDescription = "Consumo semanal. " + dias.zip(totais).joinToString { "${it.first}: ${it.second} ml" } + ". Meta atual: $meta ml"
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (totais.isEmpty()) return
        val d = resources.displayMetrics.density
        val top = 28 * d; val bottom = height - 30 * d
        val max = maxOf(totais.maxOrNull() ?: 0, meta, 1) * 1.15f
        val slot = width / 7f
        if (meta > 0) {
            paint.color = Color.rgb(183,206,221); paint.strokeWidth = d; paint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(4*d,4*d),0f)
            val y = bottom - meta / max * (bottom - top)
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
            paint.pathEffect = null
        }
        totais.forEachIndexed { i, valor ->
            val x = slot * (i + .5f)
            val y = bottom - valor / max * (bottom - top)
            paint.color = if (meta > 0 && valor >= meta) Color.rgb(33,133,120) else Color.rgb(78,151,200)
            canvas.drawRoundRect(x-slot*.24f, y, x+slot*.24f, bottom, 8*d, 8*d, paint)
            paint.color = Color.rgb(102,126,148); paint.textAlign = Paint.Align.CENTER; paint.textSize = android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_SP, 11f, resources.displayMetrics)
            canvas.drawText(valor.toString(), x, y - 5*d, paint)
            canvas.drawText(dias[i].take(5), x, height - 8*d, paint)
        }
    }
}

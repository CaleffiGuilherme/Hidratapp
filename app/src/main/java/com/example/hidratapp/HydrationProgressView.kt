package com.example.hidratapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ProgressBar
import kotlin.math.*

/** One native progress control: the ring and the soft water fill always share the same value. */
class HydrationProgressView(context: Context, attrs: AttributeSet? = null) : ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal) {
    private val brush = Paint(Paint.ANTI_ALIAS_FLAG)
    private val wave = Path()
    init { isIndeterminate = false }
    override fun setProgress(progress: Int) { super.setProgress(progress); invalidate() }
    override fun onDraw(canvas: Canvas) {
        val d = resources.displayMetrics.density
        val cx = width / 2f; val cy = height / 2f
        val radius = min(width, height) / 2f - 12*d
        val fraction = (progress.toFloat() / max.coerceAtLeast(1)).coerceIn(0f, 1f)
        brush.style = Paint.Style.FILL
        brush.color = Color.WHITE
        canvas.drawCircle(cx, cy, radius - 12*d, brush)
        val save = canvas.save()
        val clip = Path().apply { addCircle(cx,cy,radius-12*d,Path.Direction.CW) }
        canvas.clipPath(clip)
        val waterY = cy + radius - 2*radius*fraction
        for (layer in 0..1) {
            brush.color = if (layer == 0) Color.rgb(233,247,249) else Color.rgb(217,239,244)
            wave.reset(); wave.moveTo(cx-radius,cy+radius)
            for (x in 0..width step 3) {
                val y = waterY + sin(x.toFloat()/width * 2*PI + layer*1.8 + fraction*4).toFloat()*7*d + layer*7*d
                wave.lineTo(x.toFloat(), y)
            }
            wave.lineTo(cx+radius,cy+radius); wave.close(); canvas.drawPath(wave,brush)
        }
        canvas.restoreToCount(save)
        brush.style = Paint.Style.STROKE; brush.strokeWidth = 9*d; brush.strokeCap = Paint.Cap.ROUND
        brush.color = Color.rgb(219,234,245)
        val bounds = RectF(cx-radius, cy-radius, cx+radius, cy+radius)
        canvas.drawCircle(cx, cy, radius, brush)
        if (fraction > 0) {
            brush.shader = LinearGradient(cx-radius,cy-radius,cx+radius,cy+radius,
                Color.rgb(82,162,210),Color.rgb(40,121,184),Shader.TileMode.CLAMP)
            canvas.drawArc(bounds, -90f, fraction*360f, false, brush)
            brush.shader = null
            val angle = (fraction*360f - 90f)*PI/180
            brush.style = Paint.Style.FILL; brush.color = Color.WHITE
            canvas.drawCircle(cx+cos(angle).toFloat()*radius,cy+sin(angle).toFloat()*radius,3*d,brush)
        }
    }
}

package com.example.hidratapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/** A lightweight vector illustration shared by launch, setup and profile. */
class WaterIllustrationView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = minOf(width,height).toFloat()
        val checkpoint = canvas.save()
        canvas.translate((width-size)/2f,(height-size)/2f)
        canvas.scale(size/240f,size/240f)
        paint.style=Paint.Style.FILL; paint.color=Color.rgb(225,241,250)
        canvas.drawCircle(120f,120f,93f,paint)
        paint.color=Color.rgb(218,239,234);canvas.drawCircle(184f,167f,32f,paint)
        paint.color=Color.WHITE;canvas.drawCircle(58f,71f,19f,paint)
        canvas.save();canvas.rotate(-7f,119f,138f)
        path.reset();path.moveTo(76f,86f);path.lineTo(163f,86f);path.lineTo(154f,183f)
        path.quadTo(152f,194f,141f,194f);path.lineTo(98f,194f);path.quadTo(87f,194f,86f,183f);path.close()
        paint.color=Color.WHITE;canvas.drawPath(path,paint)
        val clip=canvas.save();canvas.clipPath(path)
        val water=Path().apply { moveTo(65f,134f);cubicTo(104f,115f,128f,152f,176f,129f);lineTo(176f,208f);lineTo(65f,208f);close() }
        paint.shader=LinearGradient(0f,125f,0f,200f,Color.rgb(146,205,230),Color.rgb(75,157,199),Shader.TileMode.CLAMP)
        canvas.drawPath(water,paint);paint.shader=null
        paint.color=Color.rgb(206,235,245);canvas.drawCircle(110f,158f,5f,paint);canvas.drawCircle(137f,176f,3f,paint)
        canvas.restoreToCount(clip)
        paint.style=Paint.Style.STROKE;paint.strokeWidth=3f;paint.strokeJoin=Paint.Join.ROUND;paint.color=Color.rgb(96,162,198)
        canvas.drawPath(path,paint);canvas.drawLine(83f,86f,157f,86f,paint)
        canvas.restore()
        paint.style=Paint.Style.FILL;paint.color=Color.rgb(40,121,184)
        path.reset();path.moveTo(124f,37f);path.cubicTo(119f,46f,107f,56f,107f,65f);path.cubicTo(107f,86f,140f,86f,140f,65f);path.cubicTo(140f,56f,129f,46f,124f,37f);canvas.drawPath(path,paint)
        paint.color=Color.rgb(190,222,238);canvas.drawCircle(191f,84f,6f,paint);canvas.drawCircle(48f,157f,5f,paint)
        paint.color=Color.rgb(33,133,120)
        canvas.save();canvas.rotate(-30f,177f,157f);canvas.drawOval(172f,137f,182f,160f,paint);canvas.restore()
        canvas.save();canvas.rotate(45f,184f,165f);canvas.drawOval(180f,144f,189f,164f,paint);canvas.restore()
        paint.style=Paint.Style.STROKE;paint.strokeWidth=2f;paint.strokeCap=Paint.Cap.ROUND
        canvas.drawLine(178f,151f,184f,185f,paint)
        paint.style=Paint.Style.FILL
        canvas.restoreToCount(checkpoint)
    }
}

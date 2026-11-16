package com.example.hidratapp

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.abs

/** Intercepts only horizontal movement so the surrounding page still scrolls vertically. */
class SwipeConsumoLayout(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    var aoDeslizar: ((Int) -> Unit)? = null
    private var inicioX = 0f
    private var inicioY = 0f
    private var horizontal = false
    private val tolerancia = ViewConfiguration.get(context).scaledTouchSlop
    private fun iniciar(event: MotionEvent) {
        inicioX=event.x;inicioY=event.y;horizontal=false
    }
    private fun reconhecer(event: MotionEvent) {
        val dx=abs(event.x-inicioX);val dy=abs(event.y-inicioY)
        if (!horizontal && dx>tolerancia*1.5f && dx>dy*1.4f) {
            horizontal=true
            parent?.requestDisallowInterceptTouchEvent(true)
        }
    }
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> iniciar(event)
            MotionEvent.ACTION_MOVE -> reconhecer(event)
        }
        return horizontal
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> iniciar(event)
            MotionEvent.ACTION_MOVE -> reconhecer(event)
            MotionEvent.ACTION_UP -> {
                val dx=event.x-inicioX
                if (horizontal && abs(dx)>48*resources.displayMetrics.density) aoDeslizar?.invoke(if (dx<0) 1 else -1)
                else if (!horizontal) performClick()
                horizontal=false
                parent?.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_CANCEL -> { horizontal=false;parent?.requestDisallowInterceptTouchEvent(false) }
        }
        return true
    }
    override fun performClick(): Boolean { super.performClick();return true }
}

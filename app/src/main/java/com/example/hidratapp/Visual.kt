package com.example.hidratapp

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton

/** Shared visual behavior only; data, navigation and validation stay in their activities. */
fun AppCompatActivity.aplicarVisual() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = true
        isAppearanceLightNavigationBars = true
    }
    findViewById<android.widget.ImageView>(R.id.btnHeaderVoltar)?.apply {
        setImageResource(R.drawable.ic_arrow_back)
        imageTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.blue_primary))
    }
    configurarInteracoes(findViewById(android.R.id.content))
}

@SuppressLint("ClickableViewAccessibility")
fun configurarInteracoes(view: View) {
    if (view is Button) view.isAllCaps = false
    if (view is MaterialButton || (view.isClickable && view is ViewGroup)) {
        view.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> v.animate().scaleX(.97f).scaleY(.97f).setDuration(100).start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.animate().scaleX(1f).scaleY(1f).setDuration(180).start()
            }
            false
        }
    }
    if (view is ViewGroup) for (i in 0 until view.childCount) configurarInteracoes(view.getChildAt(i))
}

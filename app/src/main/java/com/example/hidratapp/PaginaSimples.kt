package com.example.hidratapp

import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun AppCompatActivity.paginaSimples(): LinearLayout {
    androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = true
        isAppearanceLightNavigationBars = true
    }
    val d = resources.displayMetrics.density
    val padding = (24*d).toInt()
    val box = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(padding, padding, padding, padding)
        setBackgroundResource(R.drawable.bg_flo_gradient)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    val scroll = ScrollView(this).apply { isFillViewport = true; isVerticalScrollBarEnabled=false; addView(box) }
    setContentView(scroll)
    aplicarVisual()
    ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
        val b = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(b.left, b.top, b.right, b.bottom)
        insets
    }
    return box
}

package com.example.hidratapp

import android.app.Activity
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry

fun aguardarDados() {
    val instrumentacao = InstrumentationRegistry.getInstrumentation()
    val limite = SystemClock.elapsedRealtime() + 15000
    do {
        instrumentacao.waitForIdleSync()
        if (!BancoAsync.ocupado) return
        SystemClock.sleep(20)
    } while (SystemClock.elapsedRealtime() < limite)
    error("A operação de dados não terminou")
}

fun <A : Activity> ActivityScenario<A>.comDados(acao: (A) -> Unit): ActivityScenario<A> {
    aguardarDados()
    onActivity { acao(it) }
    aguardarDados()
    return this
}

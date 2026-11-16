package com.example.hidratapp

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

// Uma fila executa o trabalho do banco; o Handler entrega a resposta à tela.
object BancoAsync {
    private val fila = Executors.newSingleThreadExecutor()
    private val principal = Handler(Looper.getMainLooper())
    private val pendentes = AtomicInteger()
    val ocupado: Boolean get() = pendentes.get() > 0

    fun <T> executar(acao: () -> T, retorno: (Result<T>) -> Unit) {
        pendentes.incrementAndGet()
        fila.execute {
            val resultado = runCatching(acao)
            principal.post {
                try { retorno(resultado) } finally { pendentes.decrementAndGet() }
            }
        }
    }
}

// Base das telas com dados: não atualiza uma Activity que já foi encerrada.
abstract class DadosActivity : AppCompatActivity() {
    protected fun <T> dados(acao: BebidaRepository.() -> T, finalizar: () -> Unit = {}, sucesso: (T) -> Unit) {
        val repository = BebidaRepository(applicationContext)
        BancoAsync.executar({ repository.acao() }) { resultado ->
            if (!isDestroyed && !isFinishing) {
                finalizar()
                resultado.fold(sucesso) {
                    Toast.makeText(this, R.string.erro_dados, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

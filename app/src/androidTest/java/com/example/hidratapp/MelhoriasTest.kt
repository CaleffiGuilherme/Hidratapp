package com.example.hidratapp

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MelhoriasTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var ids = emptySet<Int>()
    private var preferencias = emptyMap<String, Map<String, *>>()

    @Before fun preparar() {
        aguardarDados()
        ids = BancoHelper(context).use { it.listar().map { b -> b.id }.toSet() }
        preferencias = listOf("perfil","celebracao").associateWith {
            context.getSharedPreferences(it, Context.MODE_PRIVATE).all.toMap()
        }
        context.getSharedPreferences("perfil",Context.MODE_PRIVATE).edit().clear()
            .putFloat("peso",70f).putInt("idade",30).putInt("altura",175).putBoolean("masculino",false).commit()
        context.getSharedPreferences("celebracao",Context.MODE_PRIVATE).edit().clear().commit()
    }
    @After fun restaurar() {
        aguardarDados()
        RelogioDoApp.agora = { System.currentTimeMillis() }
        BancoHelper(context).use { db -> db.listar().filter { it.id !in ids }.forEach { db.excluir(it.id) } }
        preferencias.forEach { (nome,valores) ->
            val editor = context.getSharedPreferences(nome,Context.MODE_PRIVATE).edit().clear()
            valores.forEach { (k,v) -> when(v) {
                is String -> editor.putString(k,v); is Int -> editor.putInt(k,v)
                is Float -> editor.putFloat(k,v); is Boolean -> editor.putBoolean(k,v)
                is Long -> editor.putLong(k,v)
            } }
            editor.commit()
        }
    }

    @Test fun repositoryExecutaForaDaThreadPrincipalERestauraSemSobrescrever() {
        val fim = CountDownLatch(1)
        var resultado: Result<Bebida>? = null
        BancoAsync.executar({
            assertNotSame(Looper.getMainLooper(),Looper.myLooper())
            val repo = BebidaRepository(context)
            val id = repo.inserir(Bebida(tipo="Café",ml=250,data="14/11/2026",hora="09:15",observacao="Teste",favorito=true)).toInt()
            val original = repo.buscarPorId(id)!!
            assertEquals(1,repo.excluir(id))
            assertNull(repo.buscarPorId(id))
            assertEquals(id.toLong(),repo.restaurar(original))
            assertTrue(runCatching { repo.restaurar(original.copy(ml=400)) }.isFailure)
            repo.buscarPorId(id)!!
        }) { resultado=it; fim.countDown() }
        assertTrue(fim.await(15,TimeUnit.SECONDS))
        val bebida=resultado!!.getOrThrow()
        assertEquals(250,bebida.ml);assertTrue(bebida.favorito);assertEquals("09:15",bebida.hora)
    }

    @Test fun desfazerExclusaoPreservaRegistroMesmoAposRecriarTela() {
        val id = BancoHelper(context).use { it.inserir(Bebida(tipo="Café",ml=200,data="14/11/2026",hora="10:30",observacao="Pausa",favorito=true)) }.toInt()
        val original = BancoHelper(context).use { it.buscarPorId(id) }
        ActivityScenario.launch<DetalheBebidaActivity>(Intent(context,DetalheBebidaActivity::class.java).putExtra("bebida_id",id)).use { tela ->
            tela.comDados { it.findViewById<Button>(R.id.btnExcluir).performClick() }
            onView(withText("Excluir")).perform(click())
            aguardarDados()
            assertNull(BancoHelper(context).use { it.buscarPorId(id) })
            tela.recreate()
            tela.comDados {
                assertFalse(it.findViewById<Button>(R.id.btnEditar).isEnabled)
                assertTrue(it.findViewById<TextView>(R.id.tvDetalheNome).text.contains("Café"))
                assertEquals("Pausa",it.findViewById<TextView>(R.id.tvDetalheObservacao).text.toString())
            }
            val limite = android.os.SystemClock.elapsedRealtime()+4000
            var visivel = false
            while (!visivel && android.os.SystemClock.elapsedRealtime()<limite) {
                tela.onActivity { a ->
                    val botao = a.findViewById<android.view.View>(com.google.android.material.R.id.snackbar_action)
                    val area = android.graphics.Rect()
                    visivel = botao != null && botao.isShown && botao.getGlobalVisibleRect(area) &&
                        area.height() >= botao.height*.9 && area.width() >= botao.width*.9
                }
                if (!visivel) android.os.SystemClock.sleep(30)
            }
            assertTrue("Desfazer deve ficar visível",visivel)
            val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            java.io.File(context.getExternalFilesDir(null), "desfazer_exclusao.png").outputStream().use {
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)
            }
            bitmap.recycle()
            onView(withText("Desfazer")).perform(click())
            aguardarDados()
            assertEquals(original,BancoHelper(context).use { it.buscarPorId(id) })
            tela.comDados { assertTrue(it.findViewById<Button>(R.id.btnEditar).isEnabled) }
        }
    }

    @Test fun painelAtualizaNaViradaECelebracaoPersistePorDia() {
        var agora = Calendar.getInstance().apply { set(2026,11,31,23,59,0);set(Calendar.MILLISECOND,0) }.timeInMillis
        RelogioDoApp.agora = { agora }
        val primeiroDia = Calculos.hoje()
        BancoHelper(context).use { it.inserir(Bebida(tipo="Água",ml=2500,data=primeiroDia,hora="23:00",observacao="Teste")) }
        ActivityScenario.launch(MainActivity::class.java).use { tela ->
            tela.comDados { assertEquals(primeiroDia,it.getSharedPreferences("celebracao",Context.MODE_PRIVATE).getString("ultimoDia",null)) }
            tela.recreate()
            tela.comDados { assertEquals(primeiroDia,it.getSharedPreferences("celebracao",Context.MODE_PRIVATE).getString("ultimoDia",null)) }
            agora = RotinaDiaria.proximaMeiaNoite(agora)+1000
            tela.comDados { it.virada.run() }
            tela.comDados { assertEquals("0 ml",it.findViewById<TextView>(R.id.tvTotal).text.toString()) }
            BancoHelper(context).use { it.inserir(Bebida(tipo="Água",ml=2500,data=Calculos.hoje(),hora="00:00",observacao="Teste")) }
            tela.comDados { it.virada.run() }
            tela.comDados { assertEquals(Calculos.hoje(),it.getSharedPreferences("celebracao",Context.MODE_PRIVATE).getString("ultimoDia",null)) }
        }
    }
}

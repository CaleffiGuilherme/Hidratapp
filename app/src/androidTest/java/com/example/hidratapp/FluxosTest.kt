package com.example.hidratapp

import android.content.Context
import android.content.SharedPreferences
import android.widget.*
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FluxosTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var prefs: SharedPreferences
    private var backup: Map<String, *> = emptyMap<String, Any?>()
    private var ids = emptySet<Int>()
    @Before fun preparar() {
        aguardarDados()
        prefs = context.getSharedPreferences("perfil", Context.MODE_PRIVATE)
        backup = prefs.all.toMap()
        prefs.edit().clear().putFloat("peso",70f).putInt("altura",175).putInt("idade",30).putBoolean("masculino",false).putInt("esporte",3).commit()
        ids = BancoHelper(context).use { it.listar().map { b -> b.id }.toSet() }
    }
    @After fun restaurar() {
        aguardarDados()
        BancoHelper(context).use { db -> db.listar().filter { it.id !in ids }.forEach { db.excluir(it.id) } }
        val editor = prefs.edit().clear()
        backup.forEach { (k,v) -> when(v) {
            is String -> editor.putString(k,v); is Int -> editor.putInt(k,v); is Float -> editor.putFloat(k,v)
            is Boolean -> editor.putBoolean(k,v); is Long -> editor.putLong(k,v)
        } }; editor.commit()
    }
    @Test fun metaDasDicasEhIgualAoPerfilComEsporte() {
        assertEquals(3500, PerfilRepository.meta(context))
        val dica = DicasEngine.gerarDicas(context, emptyList(), emptyList()).first { it.titulo == "Seu progresso de hoje" }
        assertTrue(dica.descricao.contains("3500 ml"))
    }
    @Test fun registroRapidoPodeSerDesfeito() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.comDados { it.findViewById<android.view.View>(R.id.agua200).performClick() }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            captura("inicio")
            scenario.comDados { a ->
                val total = BancoHelper(context).use { Calculos.totalValido(it.listarPorData(Calculos.hoje())) }
                assertEquals(minOf(total, PerfilRepository.meta(context)), a.findViewById<ProgressBar>(R.id.pbMeta).progress)
            }
            val novas = BancoHelper(context).use { it.listar().filter { b -> b.id !in ids } }
            assertEquals(1, novas.size); assertEquals(200, novas.single().ml); assertEquals("Água", novas.single().tipo)
            onView(withText("Desfazer")).perform(click())
            aguardarDados()
            assertEquals(ids, BancoHelper(context).use { it.listar().map { b -> b.id }.toSet() })
        }
    }
    @Test fun perfilPreservaEtapaECamposAoRecriar() {
        ActivityScenario.launch(PerfilActivity::class.java).use { s ->
            s.comDados { a -> a.findViewById<Button>(R.id.btnEditarPerfil).performClick(); a.findViewById<Button>(R.id.btnStep1Proximo).performClick(); a.findViewById<EditText>(R.id.etPeso).setText("82.5") }
            s.recreate()
            s.comDados { a -> assertEquals("Etapa 2 de 4", a.findViewById<TextView>(R.id.tvEtapaIndicador).text.toString()); assertEquals("82.5", a.findViewById<EditText>(R.id.etPeso).text.toString()) }
        }
    }
    @Test fun primeiraConfiguracaoCompletaCriaPerfilEExibeResumo() {
        prefs.edit().clear().commit()
        ActivityScenario.launch(PerfilActivity::class.java).use { scenario ->
            scenario.comDados { a -> assertEquals(android.view.View.VISIBLE,a.findViewById<android.view.View>(R.id.layoutStep0Welcome).visibility) }
            captura("onboarding_bemvindo")
            scenario.comDados { a -> a.findViewById<Button>(R.id.btnIniciarCadastro).performClick() }
            captura("onboarding_perfil")
            scenario.comDados { a ->
                a.findViewById<RadioGroup>(R.id.rgSexo).check(R.id.rbFeminino)
                a.findViewById<Button>(R.id.btnStep1Proximo).performClick()
            }
            captura("onboarding_medidas")
            scenario.comDados { a ->
                a.findViewById<EditText>(R.id.etPeso).setText("70")
                a.findViewById<EditText>(R.id.etAltura).setText("175")
                a.findViewById<EditText>(R.id.etIdade).setText("30")
                a.findViewById<Button>(R.id.btnStep2Proximo).performClick()
            }
            captura("onboarding_rotina")
            scenario.comDados { a ->
                a.findViewById<RadioGroup>(R.id.rgEsporte).check(R.id.rbEsporteModerado)
                a.findViewById<Button>(R.id.btnStep3Proximo).performClick()
            }
            captura("onboarding_cuidados")
            scenario.comDados { a ->
                a.findViewById<CheckBox>(R.id.cbDiabetes).isChecked=true
                a.findViewById<Button>(R.id.btnSalvarPerfil).performClick()
                assertEquals(android.view.View.VISIBLE,a.findViewById<android.view.View>(R.id.layoutPerfilResumo).visibility)
                assertTrue(a.findViewById<TextView>(R.id.tvResumoMeta).text.contains("3150"))
            }
            assertEquals(70f,prefs.getFloat("peso",0f));assertEquals(2,prefs.getInt("esporte",0));assertTrue(prefs.getBoolean("diabetes",false))
            captura("onboarding_pronto")
        }
    }
    @Test fun deslizarConsumoTrocaBebidasEPreservaRolagem() {
        BancoHelper(context).use { db ->
            db.inserir(Bebida(tipo="Água",ml=300,data=Calculos.hoje(),hora="09:00",observacao=""))
            db.inserir(Bebida(tipo="Suco",ml=400,data=Calculos.hoje(),hora="09:30",observacao=""))
            db.inserir(Bebida(tipo="Café",ml=200,data=Calculos.hoje(),hora="10:00",observacao=""))
            db.inserir(Bebida(tipo="Cerveja",ml=350,data=Calculos.hoje(),hora="10:30",observacao=""))
        }
        val paginas=BancoHelper(context).use { ConsumoDoDia.paginas(it.listarPorData(Calculos.hoje())) }
        fun verificar(scenario: ActivityScenario<MainActivity>, tipo: String) {
            scenario.comDados { a ->
                val esperado=paginas.first { it.titulo(context)==tipo }
                assertEquals(tipo,a.findViewById<TextView>(R.id.tvBebidaAtual).text.toString())
                val valor=java.text.NumberFormat.getIntegerInstance(java.util.Locale.forLanguageTag("pt-BR")).format(esperado.ml)
                assertEquals("$valor ml",a.findViewById<TextView>(R.id.tvTotal).text.toString())
            }
        }
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            verificar(scenario,"Água")
            onView(androidx.test.espresso.matcher.ViewMatchers.withId(R.id.areaConsumo)).perform(androidx.test.espresso.action.ViewActions.swipeLeft())
            verificar(scenario,"Suco")
            captura("consumo_suco")
            onView(androidx.test.espresso.matcher.ViewMatchers.withId(R.id.areaConsumo)).perform(androidx.test.espresso.action.ViewActions.swipeRight())
            verificar(scenario,"Água")
            scenario.comDados { a ->
                a.findViewById<android.view.View>(R.id.btnProximaBebida).performClick()
                a.findViewById<android.view.View>(R.id.btnProximaBebida).performClick()
            }
            verificar(scenario,"Café")
            scenario.recreate()
            verificar(scenario,"Café")
            scenario.comDados { it.findViewById<android.view.View>(R.id.btnProximaBebida).performClick() }
            verificar(scenario,"Cerveja")
            captura("consumo_alcool")
            scenario.comDados { a ->
                assertEquals(0,a.findViewById<ProgressBar>(R.id.pbMeta).progress)
                assertEquals("Fora da meta",a.findViewById<TextView>(R.id.tvPercentual).text.toString())
                while(a.findViewById<android.view.View>(R.id.btnProximaBebida).isEnabled) a.findViewById<android.view.View>(R.id.btnProximaBebida).performClick()
            }
            verificar(scenario,"Total do dia")
            captura("consumo_total")
            scenario.comDados { a ->
                val total=BancoHelper(context).use { Calculos.totalValido(it.listarPorData(Calculos.hoje())) }
                assertEquals(minOf(total,PerfilRepository.meta(context)),a.findViewById<ProgressBar>(R.id.pbMeta).progress)
            }
            onView(androidx.test.espresso.matcher.ViewMatchers.withId(R.id.areaConsumo)).perform(androidx.test.espresso.action.ViewActions.swipeUp())
            scenario.comDados { a -> assertTrue(a.findViewById<ScrollView>(R.id.scrollInicio).scrollY>0) }
        }
    }
    private fun captura(nome: String) {
        aguardarDados()
        android.os.SystemClock.sleep(900)
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        java.io.File(context.getExternalFilesDir(null), "$nome.png").outputStream().use {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }
    @Test fun cadastroValidaVolumeESalvaHorario() {
        ActivityScenario.launch(CadastroBebidaActivity::class.java).use { s ->
            s.comDados { a ->
                a.findViewById<EditText>(R.id.etMl).setText("0")
                a.findViewById<Button>(R.id.btnSalvar).performClick()
                assertNotNull(a.findViewById<EditText>(R.id.etMl).error)
                a.findViewById<EditText>(R.id.etMl).setText("300")
                a.findViewById<EditText>(R.id.etData).setText("14/11/2026")
                a.findViewById<EditText>(R.id.etHoraConsumo).setText("10:30")
                a.findViewById<Button>(R.id.btnSalvar).performClick()
            }
            val novo = BancoHelper(context).use { it.listar().single { b -> b.id !in ids } }
            assertEquals(300, novo.ml); assertEquals("10:30", novo.hora); assertEquals("14/11/2026", novo.data)
        }
    }
    @Test fun edicaoPreservaTipoAoRecriarESalvaNovaSelecao() {
        val id=BancoHelper(context).use { it.inserir(Bebida(tipo="Café",ml=200,data="14/11/2026",hora="10:30",observacao="Pausa",favorito=true)) }
        val intent=android.content.Intent(context,CadastroBebidaActivity::class.java).putExtra("bebida_id",id.toInt())
        ActivityScenario.launch<CadastroBebidaActivity>(intent).use { scenario ->
            scenario.comDados { a ->
                assertTrue(a.findViewById<com.google.android.material.chip.Chip>(R.id.chipCafe).isChecked)
                a.findViewById<com.google.android.material.chip.Chip>(R.id.chipSuco).performClick()
                a.findViewById<EditText>(R.id.etMl).setText("350")
                a.findViewById<EditText>(R.id.etHoraConsumo).setText("11:15")
            }
            scenario.recreate()
            scenario.comDados { a ->
                assertTrue(a.findViewById<com.google.android.material.chip.Chip>(R.id.chipSuco).isChecked)
                assertEquals("350",a.findViewById<EditText>(R.id.etMl).text.toString())
                assertEquals("11:15",a.findViewById<EditText>(R.id.etHoraConsumo).text.toString())
                a.findViewById<Button>(R.id.btnSalvar).performClick()
            }
            val bebida=BancoHelper(context).use { it.buscarPorId(id.toInt())!! }
            assertEquals("Suco",bebida.tipo);assertEquals(350,bebida.ml);assertTrue(bebida.favorito)
            assertEquals("11:15",bebida.hora);assertEquals("Pausa",bebida.observacao)
        }
    }
    @Test fun enumCriaOpcoesESalvaValorEstavelMesmoComOutroRotulo() {
        ActivityScenario.launch(CadastroBebidaActivity::class.java).use { scenario ->
            scenario.comDados { a ->
                val grupo = a.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipTipos)
                assertEquals(TipoBebida.entries.size, grupo.childCount)
                TipoBebida.entries.forEach { tipo ->
                    val chip = a.findViewById<com.google.android.material.chip.Chip>(tipo.chipId)
                    assertEquals(tipo.nome(a), chip.text.toString())
                    assertEquals(tipo, chip.tag)
                }
                val chip = a.findViewById<com.google.android.material.chip.Chip>(TipoBebida.SUCO.chipId)
                chip.text = "Suco natural"
                chip.performClick()
                a.findViewById<EditText>(R.id.etMl).setText("250")
                a.findViewById<EditText>(R.id.etData).setText("14/11/2026")
                a.findViewById<EditText>(R.id.etHoraConsumo).setText("10:00")
                a.findViewById<Button>(R.id.btnSalvar).performClick()
            }
            val novo = BancoHelper(context).use { it.listar().single { b -> b.id !in ids } }
            assertEquals(TipoBebida.SUCO.valorBanco, novo.tipo)
            assertEquals(250, novo.ml)
        }
    }
    @Test fun cadastroVisualSomaAtalhosESelecionaBebida() {
        ActivityScenario.launch(CadastroBebidaActivity::class.java).use { scenario ->
            scenario.comDados { a ->
                a.findViewById<com.google.android.material.chip.Chip>(R.id.chipCafe).performClick()
                a.findViewById<Button>(R.id.btn200).performClick()
                a.findViewById<Button>(R.id.btn300).performClick()
                assertEquals("500",a.findViewById<EditText>(R.id.etMl).text.toString())
                assertEquals("Café · 500 ml",a.findViewById<TextView>(R.id.tvResumoRegistro).text.toString())
                a.findViewById<EditText>(R.id.etData).setText("14/11/2026")
                a.findViewById<EditText>(R.id.etHoraConsumo).setText("10:00")
            }
            captura("adicionar_bebida")
            scenario.comDados { it.findViewById<Button>(R.id.btnSalvar).performClick() }
            val novo=BancoHelper(context).use { it.listar().single { b -> b.id !in ids } }
            assertEquals("Café",novo.tipo);assertEquals(500,novo.ml)
        }
    }
    @Test fun filtrosCombinamPeriodoTipoEFavoritos() {
        BancoHelper(context).use { db ->
            db.inserir(Bebida(tipo="Água",ml=300,data=Calculos.hoje(),hora="09:00",observacao="",favorito=true))
            db.inserir(Bebida(tipo="Café",ml=200,data=Calculos.hoje(),hora="10:00",observacao=""))
            db.inserir(Bebida(tipo="Água",ml=200,data="01/01/2000",hora="10:00",observacao="",favorito=true))
        }
        ActivityScenario.launch(ListaActivity::class.java).use { s ->
            s.comDados { a ->
                a.findViewById<Spinner>(R.id.filtroPeriodo).setSelection(1)
                a.findViewById<Spinner>(R.id.filtroTipo).setSelection(1)
                a.findViewById<CheckBox>(R.id.filtroFavoritos).isChecked = true
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            s.comDados { a ->
                val expected = BancoHelper(context).use { db -> db.listar().count { it.data == Calculos.hoje() && it.tipo == "Água" && it.favorito } }
                assertEquals(expected, a.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvBebidas).adapter!!.itemCount)
            }
            captura("estatisticas")
        }
    }
    @Test fun telasNovasAbremEHistoricoFiltraFavoritos() {
        val previewId = BancoHelper(context).use { it.inserir(Bebida(tipo="Água",ml=1200,data=Calculos.hoje(),hora="08:00",observacao="Prévia visual")) }
        ActivityScenario.launch(SplashActivity::class.java).use { captura("abertura") }
        ActivityScenario.launch(MainActivity::class.java).use { InstrumentationRegistry.getInstrumentation().waitForIdleSync(); captura("inicio_preview") }
        ActivityScenario.launch(SintomasActivity::class.java).use { captura("sintomas") }
        ActivityScenario.launch(SobreActivity::class.java).use { captura("sobre") }
        ActivityScenario.launch<DetalheBebidaActivity>(android.content.Intent(context,DetalheBebidaActivity::class.java).putExtra("bebida_id",previewId.toInt())).use { captura("detalhe") }
        ActivityScenario.launch(DicasActivity::class.java).use { scenario ->
            captura("dicas")
            scenario.comDados { a ->
                a.findViewById<Button>(R.id.btnExpandirDica).performClick()
                assertEquals(Int.MAX_VALUE,a.findViewById<TextView>(R.id.tvDescricaoDica).maxLines)
                a.findViewById<Button>(R.id.btnExpandirDica).performClick()
                assertEquals(3,a.findViewById<TextView>(R.id.tvDescricaoDica).maxLines)
            }
        }
        ActivityScenario.launch(PerfilActivity::class.java).use { InstrumentationRegistry.getInstrumentation().waitForIdleSync(); captura("perfil") }
        ActivityScenario.launch(CadastroBebidaActivity::class.java).use { InstrumentationRegistry.getInstrumentation().waitForIdleSync(); captura("cadastro") }
        ActivityScenario.launch(RegrasActivity::class.java).use { InstrumentationRegistry.getInstrumentation().waitForIdleSync(); captura("regras") }
        ActivityScenario.launch(LembretesActivity::class.java).use { it.recreate(); InstrumentationRegistry.getInstrumentation().waitForIdleSync(); captura("lembretes") }
        ActivityScenario.launch(ListaActivity::class.java).use { s ->
            s.comDados { a ->
                assertTrue(a.findViewById<GraficoSemanal>(R.id.graficoSemanal).contentDescription.contains("Meta atual: 3500"))
                a.findViewById<CheckBox>(R.id.filtroFavoritos).isChecked = true
            }
            s.comDados { a ->
                val expected = BancoHelper(context).use { it.listar().count { b -> b.favorito } }
                assertEquals(expected, a.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvBebidas).adapter!!.itemCount)
            }
        }
    }
}

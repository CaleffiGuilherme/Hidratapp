package com.example.hidratapp

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class LembretesActivity : AppCompatActivity() {
    private lateinit var ativo: androidx.appcompat.widget.SwitchCompat
    private lateinit var status: TextView
    private var inicio = 480
    private var fim = 1320
    private lateinit var intervalo: Spinner
    private val permissao = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
        if (permitido) salvar() else { ativo.isChecked = false; salvar(); Toast.makeText(this, getString(R.string.lembretes_permita_notificacoes_nas_configuracoes_para_receber_lembretes), Toast.LENGTH_LONG).show() }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = getSharedPreferences("lembretes", Context.MODE_PRIVATE)
        inicio = savedInstanceState?.getInt("inicio") ?: p.getInt("inicio", 480)
        fim = savedInstanceState?.getInt("fim") ?: p.getInt("fim", 1320)
        val box = paginaSimples()
        cabecalhoPagina(box,getString(R.string.lembretes_no_ritmo_da_sua_rotina),getString(R.string.lembretes_uma_pausa_para_voce),getString(R.string.lembretes_lembretes_gentis_para_cuidar_da_sua_hidratacao))
        val ativacao=secaoPagina(box,getString(R.string.lembretes_um_cuidado_que_acompanha_voce))
        ativo=com.google.android.material.switchmaterial.SwitchMaterial(this).apply {
            text=getString(R.string.lembretes_ativar_lembretes);textSize=15f;minHeight=dp(48)
            isChecked=savedInstanceState?.getBoolean("ativo") ?: p.getBoolean("ativo",false)
        }
        ativacao.addView(ativo)
        status=textoPagina("",12f,true).apply { setPadding(dp(12),dp(12),dp(12),dp(12));setBackgroundResource(R.drawable.bg_soft_blue) }
        ativacao.addView(status)
        val horarios=secaoPagina(box,getString(R.string.lembretes_seu_momento_de_pausa))
        horarios.addView(textoPagina(getString(R.string.lembretes_escolha_um_periodo_no_mesmo_dia),12f,true))
        val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        horarios.addView(row)
        lateinit var bInicio: Button
        lateinit var bFim: Button
        bInicio=botaoPagina(row,getString(R.string.lembretes_inicio, hora(inicio))) {
            TimePickerDialog(this,{ _,h,m -> inicio=h*60+m;bInicio.text=getString(R.string.lembretes_inicio, hora(inicio)) },inicio/60,inicio%60,true).show()
        }.apply { layoutParams=LinearLayout.LayoutParams(0,dp(80),1f).apply { marginEnd=dp(6) } }
        bFim=botaoPagina(row,getString(R.string.lembretes_fim, hora(fim))) {
            TimePickerDialog(this,{ _,h,m -> fim=h*60+m;bFim.text=getString(R.string.lembretes_fim, hora(fim)) },fim/60,fim%60,true).show()
        }.apply { layoutParams=LinearLayout.LayoutParams(0,dp(80),1f).apply { marginStart=dp(6) } }
        horarios.addView(textoPagina(getString(R.string.lembretes_intervalo_entre_lembretes),13f,true).apply { setPadding(0,dp(16),0,0) })
        val intervalos=listOf(30,60,90,120)
        intervalo=Spinner(this).apply {
            adapter=ArrayAdapter(this@LembretesActivity,R.layout.item_filtro_spinner,intervalos.map { getString(R.string.intervalo_minutos, it) })
            contentDescription=getString(R.string.lembretes_intervalo_entre_lembretes)
            setSelection(intervalos.indexOf(savedInstanceState?.getInt("intervalo") ?: p.getInt("intervalo",60)).coerceAtLeast(0))
            layoutParams=LinearLayout.LayoutParams(-1,dp(52));setBackgroundResource(R.drawable.bg_soft_blue)
        }
        horarios.addView(intervalo)
        botaoPagina(box,getString(R.string.lembretes_salvar_configuracoes),true) {
            if (inicio>=fim) { Toast.makeText(this,getString(R.string.lembretes_o_inicio_deve_ser_anterior_ao_fim),Toast.LENGTH_LONG).show();return@botaoPagina }
            p.edit().putInt("intervalo",intervalos[intervalo.selectedItemPosition]).putInt("inicio",inicio).putInt("fim",fim).apply()
            if (ativo.isChecked && Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED) permissao.launch(Manifest.permission.POST_NOTIFICATIONS)
            else salvar()
        }
        val ajuste=secaoPagina(box,getString(R.string.lembretes_tudo_pronto_para_lembrar))
        ajuste.addView(textoPagina(getString(R.string.lembretes_envie_um_teste_ou_ajuste_a_permissao),12f,true))
        botaoPagina(ajuste,getString(R.string.lembretes_enviar_notificacao_de_teste)) {
            if (Lembretes.permitido(this)) Lembretes.notificar(this)
            else Toast.makeText(this,getString(R.string.lembretes_habilite_as_notificacoes_primeiro),Toast.LENGTH_LONG).show()
        }
        botaoPagina(ajuste,getString(R.string.lembretes_permissoes_de_notificacao)) {
            val intent=if (Build.VERSION.SDK_INT>=26) Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,packageName)
            else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        box.addView(textoPagina(getString(R.string.lembretes_os_horarios_sao_aproximados_e_podem_atrasar),12f,true))
        aplicarVisual()
    }

    private fun hora(m: Int) = "%02d:%02d".format(m/60, m%60)
    private fun salvar() {
        getSharedPreferences("lembretes", Context.MODE_PRIVATE).edit().putBoolean("ativo", ativo.isChecked).apply()
        Lembretes.agendar(this)
        atualizarStatus()
        Toast.makeText(this, getString(R.string.lembretes_configuracoes_salvas), Toast.LENGTH_SHORT).show()
    }
    private fun atualizarStatus() { status.text = if (!Lembretes.permitido(this)) getString(R.string.lembretes_notificacoes_bloqueadas_nas_configuracoes_do_android) else if (getSharedPreferences("lembretes", MODE_PRIVATE).getBoolean("ativo", false)) getString(R.string.lembretes_lembretes_programados) else getString(R.string.lembretes_lembretes_desativados) }
    override fun onResume() { super.onResume(); if (::status.isInitialized) atualizarStatus() }
    override fun onSaveInstanceState(outState: Bundle) { outState.putBoolean("ativo", ativo.isChecked); outState.putInt("intervalo", listOf(30,60,90,120)[intervalo.selectedItemPosition]); outState.putInt("inicio", inicio); outState.putInt("fim", fim); super.onSaveInstanceState(outState) }
}

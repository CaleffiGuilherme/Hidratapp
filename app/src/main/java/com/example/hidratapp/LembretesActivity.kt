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
        if (permitido) salvar() else { ativo.isChecked = false; salvar(); Toast.makeText(this, "Permita notificações nas configurações para receber lembretes.", Toast.LENGTH_LONG).show() }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = getSharedPreferences("lembretes", Context.MODE_PRIVATE)
        inicio = savedInstanceState?.getInt("inicio") ?: p.getInt("inicio", 480)
        fim = savedInstanceState?.getInt("fim") ?: p.getInt("fim", 1320)
        val box = paginaSimples()
        cabecalhoPagina(box,"NO RITMO DA SUA ROTINA","Uma pausa para você.","Lembretes gentis para cuidar da sua hidratação, nos horários que combinam com seu dia.")
        val ativacao=secaoPagina(box,"Um cuidado que acompanha você")
        ativo=com.google.android.material.switchmaterial.SwitchMaterial(this).apply {
            text="Ativar lembretes";textSize=15f;minHeight=dp(48)
            isChecked=savedInstanceState?.getBoolean("ativo") ?: p.getBoolean("ativo",false)
        }
        ativacao.addView(ativo)
        status=textoPagina("",12f,true).apply { setPadding(dp(12),dp(12),dp(12),dp(12));setBackgroundResource(R.drawable.bg_soft_blue) }
        ativacao.addView(status)
        val horarios=secaoPagina(box,"Seu momento de pausa")
        horarios.addView(textoPagina("Escolha um período no mesmo dia.",12f,true))
        val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        horarios.addView(row)
        lateinit var bInicio: Button
        lateinit var bFim: Button
        bInicio=botaoPagina(row,"Início\n${hora(inicio)}") {
            TimePickerDialog(this,{ _,h,m -> inicio=h*60+m;bInicio.text="Início\n${hora(inicio)}" },inicio/60,inicio%60,true).show()
        }.apply { layoutParams=LinearLayout.LayoutParams(0,dp(80),1f).apply { marginEnd=dp(6) } }
        bFim=botaoPagina(row,"Fim\n${hora(fim)}") {
            TimePickerDialog(this,{ _,h,m -> fim=h*60+m;bFim.text="Fim\n${hora(fim)}" },fim/60,fim%60,true).show()
        }.apply { layoutParams=LinearLayout.LayoutParams(0,dp(80),1f).apply { marginStart=dp(6) } }
        horarios.addView(textoPagina("Intervalo entre lembretes",13f,true).apply { setPadding(0,dp(16),0,0) })
        val intervalos=listOf(30,60,90,120)
        intervalo=Spinner(this).apply {
            adapter=ArrayAdapter(this@LembretesActivity,R.layout.item_filtro_spinner,intervalos.map { "$it minutos" })
            contentDescription="Intervalo entre lembretes"
            setSelection(intervalos.indexOf(savedInstanceState?.getInt("intervalo") ?: p.getInt("intervalo",60)).coerceAtLeast(0))
            layoutParams=LinearLayout.LayoutParams(-1,dp(52));setBackgroundResource(R.drawable.bg_soft_blue)
        }
        horarios.addView(intervalo)
        botaoPagina(box,"Salvar configurações",true) {
            if (inicio>=fim) { Toast.makeText(this,"O início deve ser anterior ao fim.",Toast.LENGTH_LONG).show();return@botaoPagina }
            p.edit().putInt("intervalo",intervalos[intervalo.selectedItemPosition]).putInt("inicio",inicio).putInt("fim",fim).apply()
            if (ativo.isChecked && Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED) permissao.launch(Manifest.permission.POST_NOTIFICATIONS)
            else salvar()
        }
        val ajuste=secaoPagina(box,"Tudo pronto para lembrar?")
        ajuste.addView(textoPagina("Envie um teste ou ajuste a permissão do Android.",12f,true))
        botaoPagina(ajuste,"Enviar notificação de teste") {
            if (Lembretes.permitido(this)) Lembretes.notificar(this)
            else Toast.makeText(this,"Habilite as notificações primeiro.",Toast.LENGTH_LONG).show()
        }
        botaoPagina(ajuste,"Permissões de notificação") {
            val intent=if (Build.VERSION.SDK_INT>=26) Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,packageName)
            else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        box.addView(textoPagina("Os horários são aproximados e podem atrasar com a economia de bateria do Android. Toque em Salvar para aplicar alterações.",12f,true))
        aplicarVisual()
    }

    private fun hora(m: Int) = "%02d:%02d".format(m/60, m%60)
    private fun salvar() {
        getSharedPreferences("lembretes", Context.MODE_PRIVATE).edit().putBoolean("ativo", ativo.isChecked).apply()
        Lembretes.agendar(this)
        atualizarStatus()
        Toast.makeText(this, "Configurações salvas", Toast.LENGTH_SHORT).show()
    }
    private fun atualizarStatus() { status.text = if (!Lembretes.permitido(this)) "Notificações bloqueadas nas configurações do Android." else if (getSharedPreferences("lembretes", MODE_PRIVATE).getBoolean("ativo", false)) "Lembretes programados." else "Lembretes desativados." }
    override fun onResume() { super.onResume(); if (::status.isInitialized) atualizarStatus() }
    override fun onSaveInstanceState(outState: Bundle) { outState.putBoolean("ativo", ativo.isChecked); outState.putInt("intervalo", listOf(30,60,90,120)[intervalo.selectedItemPosition]); outState.putInt("inicio", inicio); outState.putInt("fim", fim); super.onSaveInstanceState(outState) }
}

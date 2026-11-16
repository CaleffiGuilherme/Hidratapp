package com.example.hidratapp

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

object Lembretes {
    const val CANAL = "hidratacao"
    fun permitido(c: Context): Boolean {
        val canalAtivo = Build.VERSION.SDK_INT < 26 || c.getSystemService(NotificationManager::class.java)
            .getNotificationChannel(CANAL)?.importance != NotificationManager.IMPORTANCE_NONE
        return canalAtivo && NotificationManagerCompat.from(c).areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(c, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }
    private fun pending(c: Context) = PendingIntent.getBroadcast(c, 100, Intent(c, LembreteReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    fun agendar(c: Context) {
        val alarm = c.getSystemService(AlarmManager::class.java)
        alarm.cancel(pending(c))
        val p = c.getSharedPreferences("lembretes", Context.MODE_PRIVATE)
        if (!p.getBoolean("ativo", false)) return
        val proximo = proximoHorario(System.currentTimeMillis(), p.getInt("inicio", 8*60), p.getInt("fim", 22*60), p.getInt("intervalo", 60))
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, proximo, pending(c))
    }
    fun proximoHorario(agora: Long, inicio: Int, fim: Int, intervalo: Int): Long {
        require(inicio in 0..1439 && fim in 1..1439 && inicio < fim && intervalo > 0)
        val base = Calendar.getInstance().apply { timeInMillis = agora; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        for (dia in 0..1) {
            var minuto = inicio
            while (minuto <= fim) {
                val candidato = (base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, dia); add(Calendar.MINUTE, minuto) }.timeInMillis
                if (candidato > agora) return candidato
                minuto += intervalo
            }
        }
        error("Horário inválido")
    }
    fun notificar(c: Context) {
        if (!permitido(c)) return
        if (Build.VERSION.SDK_INT >= 26) c.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CANAL, "Lembretes de hidratação", NotificationManager.IMPORTANCE_DEFAULT))
        val abrir = PendingIntent.getActivity(c, 101, Intent(c, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        try {
            NotificationManagerCompat.from(c).notify(100, NotificationCompat.Builder(c, CANAL)
                .setSmallIcon(R.drawable.ic_bell_notification).setContentTitle("Sua pausa para hidratação")
                .setContentText("Que tal verificar seu consumo e registrar uma bebida?")
                .setContentIntent(abrir).setAutoCancel(true).build())
        } catch (_: SecurityException) { /* Permissão pode ser revogada pelo sistema. */ }
    }
}

class LembreteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val p = context.getSharedPreferences("lembretes", Context.MODE_PRIVATE)
        if (!p.getBoolean("ativo", false)) return
        if (intent.action == null) {
            val agora = Calendar.getInstance()
            val minuto = agora.get(Calendar.HOUR_OF_DAY)*60 + agora.get(Calendar.MINUTE)
            if (minuto in p.getInt("inicio", 480)..p.getInt("fim", 1320)) Lembretes.notificar(context)
        }
        Lembretes.agendar(context)
    }
}

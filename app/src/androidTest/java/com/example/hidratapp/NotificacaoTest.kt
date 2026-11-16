package com.example.hidratapp

import android.app.NotificationManager
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificacaoTest {
    @Test fun publicaNotificacaoNoCanalDeHidratacao() {
        val i = InstrumentationRegistry.getInstrumentation()
        val c = i.targetContext
        if (Build.VERSION.SDK_INT >= 33) {
            i.uiAutomation.executeShellCommand("pm grant ${c.packageName} android.permission.POST_NOTIFICATIONS").use { fd ->
                android.os.ParcelFileDescriptor.AutoCloseInputStream(fd).use { it.readBytes() }
            }
        }
        val manager = c.getSystemService(NotificationManager::class.java)
        try {
            assertTrue(Lembretes.permitido(c))
            Lembretes.notificar(c)
            val limite = android.os.SystemClock.elapsedRealtime() + 5000
            var publicada = manager.activeNotifications.firstOrNull { it.id == 100 }
            while (publicada == null && android.os.SystemClock.elapsedRealtime() < limite) {
                android.os.SystemClock.sleep(50)
                publicada = manager.activeNotifications.firstOrNull { it.id == 100 }
            }
            assertNotNull("Notificação deve ser publicada pelo serviço do Android", publicada)
            val notification = publicada!!.notification
            assertEquals("Sua pausa para hidratação", notification.extras.getString("android.title"))
            if (Build.VERSION.SDK_INT >= 26) assertEquals(Lembretes.CANAL, notification.channelId)
        } finally { manager.cancel(100) }
    }
}

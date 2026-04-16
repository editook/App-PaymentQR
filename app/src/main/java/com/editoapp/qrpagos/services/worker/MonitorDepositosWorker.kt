package com.editoapp.qrpagos.services.worker

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

class MonitorDepositosWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val NOTIFICATION_ID_BASE = 2000
        private const val CHANNEL_ID = "depositos_channel"

        // Lista compartida (en producción usa Room o DataStore)
        private val depositosPendientes = mutableListOf<Deposito>()

        @Synchronized
        fun agregarDeposito(monto: Double, origen: String) {
            depositosPendientes.add(Deposito(monto, origen, System.currentTimeMillis()))
        }

        @Synchronized
        fun obtenerYEliminarDepositos(): List<Deposito> {
            val copia = depositosPendientes.toList()
            depositosPendientes.clear()
            return copia
        }
    }

    data class Deposito(
        val monto: Double,
        val origen: String,
        val timestamp: Long
    )

    override suspend fun doWork(): Result {
        try {
            // 1. Configurar foreground con notificación persistente
            setForeground(createForegroundInfo())

            // 2. Bucle infinito controlado (cada 10 segundos)
            while (true) {  // isActive se vuelve false cuando WorkManager detiene el worker

                val nuevosDepositos = obtenerDepositosAsync()

                nuevosDepositos.forEach { deposito ->
                    agregarDeposito(deposito.monto, deposito.origen)
                }

                // Verificar si hay elementos en la lista
                val depositosParaNotificar = obtenerYEliminarDepositos()

                if (depositosParaNotificar.isNotEmpty()) {
                    // Lanzar notificación individual por cada depósito
                    depositosParaNotificar.forEach { deposito ->
                        mostrarNotificacionDepositoIndividual(deposito)
                    }
                }

                // Esperar 10 segundos antes de la siguiente iteración
                delay(10000)
            }

            return Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }

    // Función asincrónica que simula obtener depósitos de una API o BD
    private suspend fun obtenerDepositosAsync(): List<Deposito> {
        // Simular llamada a API, Firebase, o base de datos local
        delay(500) // Simula latencia de red

        // EJEMPLO: Aquí consultarías tu fuente de datos real
        // Por ahora, simulamos depósitos aleatorios para prueba
        return if (kotlin.random.Random.nextBoolean()) {
            // Simular que a veces hay depósitos nuevos
            listOf(
                Deposito(
                    monto = (100..5000).random().toDouble(),
                    origen = "Cuenta ${(1..999).random()}",
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            emptyList()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle("Monitor de depósitos activo")
            .setContentText("Verificando depósitos cada 10 segundos...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        return ForegroundInfo(NOTIFICATION_ID_BASE, notification)
    }

    private fun mostrarNotificacionDepositoIndividual(deposito: Deposito) {
        createNotificationChannel()

        val notificationId = (NOTIFICATION_ID_BASE + 1 + deposito.timestamp % 1000).toInt()

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle("¡Nuevo depósito detectado!")
            .setContentText("Depósito de $${deposito.monto} desde ${deposito.origen}")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Monto: $${deposito.monto}\nOrigen: ${deposito.origen}\nHora: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(deposito.timestamp)}")
            )
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Monitor de Depósitos",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando se detectan nuevos depósitos"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
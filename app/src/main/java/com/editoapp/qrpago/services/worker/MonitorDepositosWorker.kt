package com.editoapp.qrpago.services.worker

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.delay

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

        val nuevosDepositos = obtenerDepositosAsync()

        nuevosDepositos.forEach { deposito ->
            mostrarNotificacionDepositoIndividual(deposito)
        }
        val depositosParaNotificar = obtenerYEliminarDepositos()

        if (depositosParaNotificar.isNotEmpty()) {

            depositosParaNotificar.forEach { deposito ->
                mostrarNotificacionDepositoIndividual(deposito)
            }
        }
        return Result.success()

    }

    private suspend fun obtenerDepositosAsync(): List<Deposito> {

        delay(500) // Simula latencia de red

        return if (true) {
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
    private fun mostrarNotificacionDepositoIndividual(deposito: Deposito) {
        createNotificationChannel()

        val notificationId = (NOTIFICATION_ID_BASE + 1 + deposito.timestamp % 1000).toInt()

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle("¡Nuevo depósito detectado!")
            .setContentText("Depósito de $${deposito.monto} desde ${deposito.origen}")
            .setSmallIcon(android.R.drawable.ic_notification_clear_all)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Monitor de Depósitos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando se detectan nuevos depósitos"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
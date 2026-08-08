package com.editoapp.qrpago.services.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.editoapp.qrpago.data.HelpText.DataKeys
import com.editoapp.qrpago.viewmodel.AppEvents

class MonitorDisplayWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(DataKeys.TITLE) ?: ""
        val value = inputData.getString(DataKeys.VALUE) ?: ""
        if(value != "" && title != ""){
            mostrarNotificacion(value,title)
            AppEvents.triggerRefreshInfoData()
            AppEvents.triggerRefreshPayments()
        }

        return Result.success()
    }

    private fun mostrarNotificacion(value: String,title: String) {

        val channelId = "pagos_channel"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Crear canal (Android 8+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Pagos",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(value)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
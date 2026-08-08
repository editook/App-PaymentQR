package com.editoapp.qrpago.services

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.editoapp.qrpago.data.HelpText.DataKeys
import com.editoapp.qrpago.services.worker.MonitorDepositosWorker
import com.editoapp.qrpago.services.worker.MonitorDisplayWorker
import com.editoapp.qrpago.utils.FirebaseTokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationFirebaseService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseTokenManager.setToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.let { data->
            val action = data["accion"]
            if(action == "new_event"){//envia a todos los dispositivos
                val workRequest = OneTimeWorkRequestBuilder<MonitorDepositosWorker>().build()
                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            }
            if(action == "new_pay"){
                val title = data[DataKeys.TITLE] ?: ""
                val value = data[DataKeys.VALUE] ?: ""
                if(title != ""){
                    val inputData = workDataOf(
                        DataKeys.TITLE to title,
                        DataKeys.VALUE to value
                    )

                    val workRequest = OneTimeWorkRequestBuilder<MonitorDisplayWorker>()
                        .setInputData(inputData)
                        .build()

                    WorkManager.getInstance(applicationContext).enqueue(workRequest)
                }

            }
        }
    }
}
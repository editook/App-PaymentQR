package com.editoapp.qrpagos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.editoapp.qrpagos.services.worker.MonitorDepositosWorker
import com.editoapp.qrpagos.ui.theme.AppRoot
import com.editoapp.qrpagos.ui.theme.ScreenBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startMonitorDeposits()
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScreenBg)
            ) {

                AppRoot()
            }

        }

    }

    private fun startMonitorDeposits() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<MonitorDepositosWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "monitor_continuo",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

}
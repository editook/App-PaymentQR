package com.editoapp.qrpago

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.editoapp.qrpago.ui.theme.AppRoot
import com.editoapp.qrpago.ui.theme.ScreenBg
import com.editoapp.qrpago.utils.FirebaseTokenManager
import com.editoapp.qrpago.viewmodel.AppViewModel
import com.google.firebase.messaging.FirebaseMessaging
class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(context) as T
    }
}
class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels {
        AppViewModelFactory(applicationContext)
    }
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

                AppRoot(vm)
            }

        }

    }
    override fun onResume() {
        super.onResume()
        vm.isAppVisible = true
    }

    override fun onPause() {
        super.onPause()
        vm.isAppVisible = false
    }
    private fun startMonitorDeposits() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        //envia a todos los dispositivos
        FirebaseMessaging.getInstance().subscribeToTopic("allpagosqr")
        .addOnCompleteListener { task ->
            if(task.isSuccessful){
                println("Sucrito")
            }
        }
        //envia a un dispositivo
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener
                val token = task.result
                FirebaseTokenManager.setToken(token)
            }
    }

}
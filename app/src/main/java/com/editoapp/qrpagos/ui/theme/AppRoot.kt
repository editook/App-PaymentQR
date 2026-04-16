package com.editoapp.qrpagos.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.editoapp.qrpagos.data.UserType
import com.editoapp.qrpagos.ui.theme.utils.LoadingQRView
import com.editoapp.qrpagos.viewmodel.AppViewModel

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val vm = remember { AppViewModel(context) }
    Scaffold(
        bottomBar = {
            if (vm.currentView != "login") {
                if (vm.user.type == UserType.BRANCH) {
                    BottomNavBarBranch(vm)
                }
                else{
                    BottomNavBar(vm)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (vm.user.type == UserType.BRANCH) {
                when (vm.currentView) {
                    "login" -> LoginScreen(vm)
                    "main" -> MainHome(vm)
                    "history" -> HistoryScreen(vm)
                    "qrview" -> LoadingQRView(vm)
                    "generate" -> MainScreen(vm)
                    "createPos" -> CreatePosScreen(vm)
                }
            }
            else{
                when (vm.currentView) {
                    "login" -> LoginScreen(vm)
                    "main" -> MainHome(vm)
                    "history" -> HistoryScreen(vm)
                    "qrview" -> LoadingQRView(vm)
                    "generate" -> MainScreen(vm)
                    "createPos" -> CreatePosScreen(vm)
                }
            }
        }
    }
}

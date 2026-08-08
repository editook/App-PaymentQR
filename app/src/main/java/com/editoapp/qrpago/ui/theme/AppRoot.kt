package com.editoapp.qrpago.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.editoapp.qrpago.data.UserType
import com.editoapp.qrpago.ui.theme.utils.LoadingQRView
import com.editoapp.qrpago.viewmodel.AppViewModel

@Composable
fun AppRoot(vm: AppViewModel) {

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

package com.editoapp.qrpagos.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.editoapp.qrpagos.R
import com.editoapp.qrpagos.viewmodel.AppViewModel
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun BottomNavBarBranchPreview() {
    val context = LocalContext.current
    val vm = AppViewModel(context)
    BottomNavBarBranch(vm)
}
@Composable
fun BottomNavBarBranch(vm: AppViewModel) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp).background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter,

    ) {

        // 🌟 Barra flotante tipo cápsula
        Row(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(20.dp, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(BackgroundColor)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomFabItem(
                selected = vm.currentView == "main",
                icon = Icons.Default.Home
            ) { vm.currentView = "main" }

            BottomFabItem(
                selected = vm.currentView == "createPos",
                icon = Icons.Default.Person,
                enabled = vm.user.lvlPlan != 1
            ) { vm.currentView = "createPos" }

            Spacer(Modifier.size(56.dp))

            BottomFabItem(
                selected = vm.currentView == "history",
                icon = ImageVector.vectorResource(R.drawable.history)
            ) { vm.currentView = "history" }

            BottomFabItem(
                selected = false,
                icon = Icons.Default.Close
            ) { showLogoutDialog = true }
        }

        // 🔥 FAB PRINCIPAL (protagonista)
        FloatingActionButton(
            onClick = {
                vm.currentView = "generate"
                vm.refreshToken()
            },
            containerColor = PrimaryColor,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(72.dp)
                .offset(y = (-34).dp)
                .shadow(24.dp, CircleShape)
        ) {
            Icon(painterResource(R.drawable.qr), contentDescription = null)
        }
    }

    LogoutDialog(showLogoutDialog,
        onConfirm = {
            showLogoutDialog = false
            vm.logout()
        },
        onDismiss = {
            showLogoutDialog = false
        }
    )
}

package com.editoapp.qrpago.ui.theme.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.editoapp.qrpago.R
import com.editoapp.qrpago.data.MessageType
import com.editoapp.qrpago.data.Payment
import com.editoapp.qrpago.ui.theme.BackgroundSpanColor
import com.editoapp.qrpago.ui.theme.PrimaryColor
import com.editoapp.qrpago.ui.theme.ScreenBg
import com.editoapp.qrpago.ui.theme.SuccessColor
import com.editoapp.qrpago.viewmodel.AppViewModel
@Composable
fun LoadingDataView(vm: AppViewModel) {
    if (vm.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LinearProgressIndicator(
                progress = vm.progressHistory,
                modifier = Modifier.fillMaxWidth(0.9f).height(5.dp),
                color = PrimaryColor,
                trackColor = BackgroundSpanColor
            )

        }
    }
}

@Composable
fun CardPaymentHistory(payment: Payment,show: Boolean){
    var expanded by remember { mutableStateOf(false) }
    val padding = if (!show) 16.dp else 2.dp
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(ScreenBg.toArgb())
        ),
        onClick = { if(show){expanded = !expanded} }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = padding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)){
                Text(payment.originName,maxLines = 1, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                Text(payment.note, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(horizontalAlignment = Alignment.End) {
                    Text("+Bs. ${payment.amount}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                    Text("${payment.date} ${payment.time}", color = Color.Gray)
                }
                if(show){
                    IconButton(
                        onClick = { expanded = !expanded }
                    ) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = if (expanded)
                                painterResource(R.drawable.expand_less)
                            else
                                painterResource(R.drawable.expand_more),
                            contentDescription = "Expandir",
                        )
                    }
                }

            }

        }
        if(show){
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 0.dp, bottom = 16.dp, top = 0.dp)
                ) {

                    Divider()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nro. transaccion: ${payment.voucherId}", fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))
                    if(!payment.sourceBank.isEmpty()){
                        Text("Intermediario: ${payment.sourceBank}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

    }
    Spacer(Modifier.height(12.dp))
}
@Composable
fun CreateAlertBox(vm: AppViewModel) {
    if (vm.showDialog) {
        MessageDialog(
            message = vm.dialogMessage,
            type = vm.dialogType,
            onDismiss = { vm.showDialog = false }
        )
    }
}
@Composable
fun MessageDialog(
    message: String,
    type: MessageType,
    onDismiss: () -> Unit
) {

    val (color, icon, title) = when (type) {
        MessageType.ERROR -> Triple(
            Color(0xFFE53935),
            Icons.Default.Clear,
            "Error"
        )

        MessageType.WARNING -> Triple(
            Color(0xFFFB8C00),
            Icons.Default.Warning,
            "Advertencia"
        )

        MessageType.INFO -> Triple(
            Color(0xFF1E88E5),
            Icons.Default.Info,
            "Información"
        )
        MessageType.SUCCESS -> Triple(
            SuccessColor,
            Icons.Default.CheckCircle,
            "Exitoso"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text("Cerrar", color = Color.White)
            }
        },

        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(title)
            }
        },

        text = {
            Text(
                text = message,
                color = Color.DarkGray
            )
        }
    )
}
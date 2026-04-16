package com.editoapp.qrpagos.ui.theme.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.editoapp.qrpagos.R
import com.editoapp.qrpagos.data.QrStatus
import com.editoapp.qrpagos.ui.theme.AccentColor
import com.editoapp.qrpagos.ui.theme.BackgroundColor
import com.editoapp.qrpagos.ui.theme.BackgroundSpanColor
import com.editoapp.qrpagos.ui.theme.BlackLow
import com.editoapp.qrpagos.ui.theme.BlackLowText
import com.editoapp.qrpagos.ui.theme.BlackText
import com.editoapp.qrpagos.ui.theme.CircleIcon
import com.editoapp.qrpagos.ui.theme.ErrorColor
import com.editoapp.qrpagos.ui.theme.InfoColorTint
import com.editoapp.qrpagos.ui.theme.PurpleTop
import com.editoapp.qrpagos.ui.theme.ScreenBg
import com.editoapp.qrpagos.ui.theme.SecondColor
import com.editoapp.qrpagos.ui.theme.SuccessColor
import com.editoapp.qrpagos.ui.theme.SuccessColorTint
import com.editoapp.qrpagos.ui.theme.WarningColor
import com.editoapp.qrpagos.ui.theme.WhiteText
import com.editoapp.qrpagos.viewmodel.AppViewModel


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoadingQRViewPreview() {
    val context = LocalContext.current
    val vm  = AppViewModel(LocalContext.current)
    LoadingQRView(vm)
}

@Composable
fun LoadingQRView(vm: AppViewModel) {
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFD7CBEF),
            ScreenBg,
            ScreenBg
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {

        Column {

            Spacer(Modifier.height(10.dp))

            QrCardModal(vm)
        }
    }
    CreateAlertBox(vm)
}

@Composable
fun QrCardModal(vm: AppViewModel) {
    var bitmap: Bitmap? = null
    if(vm.qrbase64Modal != null){
        bitmap = vm.base64ToBitmap(vm.qrbase64Modal)
    }
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {

        Column(Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(.4f), CircleShape)
                        .clickable{
                            vm.sendUserToHome()
                                  },
                    contentAlignment = Alignment.Center
                ) {
                    if(!vm.isEnabledModal){
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = PurpleTop)
                    }
                    else{
                        Icon(painterResource(R.drawable.saveqr), contentDescription = null, tint = PurpleTop)
                    }

                }


                Text(
                    "Pago QR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondColor
                )

                Box {
                    CircleIcon(Icons.Default.Share, vm)
                }
            }

            Divider(Modifier.padding(vertical = 12.dp))

            if(bitmap != null){
                Image(bitmap.asImageBitmap(), null,
                    modifier = Modifier.size(400.dp),
                    contentScale = ContentScale.Fit)
            }
            else{
                Image(
                    painter = painterResource(id = R.drawable.qrdefault),
                    null,
                    modifier = Modifier.size(400.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Row(horizontalArrangement = Arrangement.Center) {
                AmountSectionModal(vm)
            }
        }
    }
}
@Composable
fun AmountSectionModal(vm: AppViewModel) {
    val size = 17.sp
    val fontWeight = FontWeight.Medium
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bs "+vm.amountQRModal,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BlackText
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Vigente hasta "+vm.qrExpirationModal,
            color = BlackLow,
            fontSize = size,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )

        Text(
            text = vm.infoData.fullName,
            fontSize = 21.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            color = BlackLowText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Numero de cuenta: ${vm.infoData.AccountNumber}",
            color = BlackLow,
            fontWeight = fontWeight,
            fontSize = size,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Concepto: "+vm.noteQRModal,
            color = BlackLow,
            fontWeight = fontWeight,
            fontSize = size,
            textAlign = TextAlign.Center
        )
        if(vm.isEnabledModal){
            VerificatePaymentModal(vm)
        }

    }
}

@Composable
fun VerificatePaymentModal(vm: AppViewModel) {
    val color = when (vm.qrStatus) {
        QrStatus.SUCCESS -> (SuccessColor)
        QrStatus.ERROR -> ErrorColor
        QrStatus.PENDING -> WarningColor
        QrStatus.NONE -> BlackLowText
    }
    val text = when (vm.qrStatus) {
        QrStatus.SUCCESS -> "PAGO REALIZADO"
        QrStatus.ERROR -> "ERROR"
        QrStatus.PENDING -> "PENDIENTE"
        QrStatus.NONE -> ""
    }
    Spacer(Modifier.height(20.dp))
    Text(
        text = text,
        fontSize = 24.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold,
        color = color
    )
    Spacer(Modifier.height(25.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Box(
            modifier = Modifier
                .border(
                    width = 1.5.dp,
                    color = AccentColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(WhiteText, RoundedCornerShape(10.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clickable{
                    vm.currentView = "generate"
                    vm.resetQrGenerate()
                }
        ) {
            Text("Cerrar",textAlign = TextAlign.Center, modifier = Modifier.width(100.dp), color = AccentColor, fontSize = 13.sp)
        }
        Box(
            modifier = Modifier
                .border(
                    width = 1.5.dp,
                    color = SuccessColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(WhiteText, RoundedCornerShape(10.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable{
                vm.checkStatusQr()
            }
        ) {
            Text("Verificar Pago",textAlign = TextAlign.Center, modifier = Modifier.width(100.dp), color = SuccessColor, fontSize = 12.sp)
        }

    }
}
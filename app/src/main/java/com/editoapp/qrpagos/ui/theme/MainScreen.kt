package com.editoapp.qrpagos.ui.theme

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.editoapp.qrpagos.R
import com.editoapp.qrpagos.data.InfoData
import com.editoapp.qrpagos.viewmodel.AppViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {

    val vm  = AppViewModel(LocalContext.current)
    vm.dateqrfinish = "12/26"
    vm.infoData = InfoData(2000.00,"Jose Martinez Seballo","Jose Martinez",listOf(),listOf(),"12345678")
    MainScreen(vm)
}

@Composable
fun TopBar(vm: AppViewModel) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        CircleIcon(null,vm)

        Text(
            "QR Payment",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SecondColor
        )

        Box {
            CircleIcon(Icons.Default.Share, vm)
        }
    }
}
@Composable
fun QrCard(vm: AppViewModel) {

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ScreenBg),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(Modifier.padding(20.dp)) {

            // Header card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    "Escaneo de QR",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = SecondColor
                )

                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.clickable { vm.openModalQR() }) {
                    Text("Ver detalles", color = if(!vm.qrbase64.isEmpty()){BlackText}else{Color.Gray})
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = PrimaryColor
                    )
                }
            }

            Divider(Modifier.padding(vertical = 12.dp))

            Row {

                QrBox(vm.qrbase64.isEmpty())

                Spacer(Modifier.width(16.dp))

                AmountSection(vm)
            }
        }
    }
}
@Composable
fun QrBox(isqrlocal: Boolean) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .background(Color.White, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(if(isqrlocal){R.drawable.qrdefault}else{R.drawable.qrmin}),
            contentDescription = null,
            modifier = Modifier.size(90.dp)
        )
    }
}
@Composable
fun AmountSection(vm: AppViewModel) {

    Column(verticalArrangement = Arrangement.SpaceBetween) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Monto", color = BlackLow)

            SecuredBadge()
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                "Bs. ${vm.amountqr}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SecondColor
            )
        }

        Spacer(Modifier.height(12.dp))

        AccountInfo(vm)
    }
}
@Composable
fun SecuredBadge() {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = SuccessColor,
                shape = RoundedCornerShape(10.dp)
            )
            .background(SuccessColorTint, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text("Seguro", color = SuccessColor, fontSize = 12.sp)
    }
}
@Composable
fun AccountInfo(vm: AppViewModel) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = PurpleTop)
        }

        Spacer(Modifier.width(10.dp))

        Column {
            Text("Cuenta", color = BlackLow, fontSize = 12.sp)
            Text(vm.infoData.getShortAccountNumber(), fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 10.dp)) {
            Text("Expira", color = BlackLow, fontSize = 12.sp)
            Text(vm.dateqrfinish, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
fun CircleIcon(icon: ImageVector?, vm: AppViewModel) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(Color.White.copy(.4f), CircleShape).clickable{
                if(icon != null){
                    val status = vm.qrbase64.isEmpty()
                    if(!status){
                        val bitmap = vm.base64ToBitmap(vm.qrbase64)
                        if(bitmap != null){
                            vm.shareBitmap(context, bitmap)
                        }

                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if(icon != null){
            val status = vm.qrbase64.isEmpty()
            Icon(icon, contentDescription = null, tint = if(status){PurpleTopLow}else{PurpleTop})
        }
        else{
            val progress = vm.tokenSeconds / 60f
            LaunchedEffect(Unit) {
                vm.startTokenTimer()
            }
            Box(
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    progress = progress,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(90.dp),
                    color = when {
                        vm.tokenSeconds > 20 -> SuccessColor
                        vm.tokenSeconds > 10 -> WarningColor
                        vm.tokenSeconds > 5 -> ErrorColor
                        else -> ErrorColor
                    }
                )

                Text(
                    text = "${vm.tokenSeconds}s",
                    fontWeight = FontWeight.Bold,
                    color = SecondColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Composable
fun QrPaymentScreen(vm: AppViewModel) {

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
            .verticalScroll(rememberScrollState())
            .background(bgGradient)
            .padding(16.dp)
    ) {

        Column {

            TopBar(vm)

            Spacer(Modifier.height(17.dp))

            QrCard(vm)

            FormCard(vm)
        }
    }
}

@Composable
fun FormCard(vm: AppViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            BalanceHeader(vm)

            Spacer(Modifier.height(10.dp))

            TransferLimit(vm)


            Spacer(Modifier.height(20.dp))

            NumberPad(
                onNumber = { vm.onNumberPressed(it) },
                onDelete = { vm.onNumberDelete() },
                onEnter = { vm.onNumberPoint() }
            )
        }
        Spacer(Modifier.height(20.dp))
        ConfirmButton {
            vm.generateQr()
        }
    }
}
@Composable
fun TransferLimit(vm: AppViewModel) {
    var expanded by remember { mutableStateOf(false) }
    Column {


        Row(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Extras", color = BlackLow)
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = PrimaryColor
            )
        }

    }
    AnimatedVisibility(visible = expanded) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    "Configuración del QR",
                    fontWeight = FontWeight.Bold,
                    color = SecondColor
                )

                Divider()

                NoteInput(vm)

                ExpirationSelector(vm)

                AcceptSinglePay(vm)
            }
        }
    }
}

@Composable
fun NoteInput(vm: AppViewModel) {

    Column {

        Text(
            "Motivo (Opcional)",
            color = Color.Gray,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = vm.inputQrNote,
            onValueChange = { vm.inputQrNote = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                Text("${vm.inputQrNote.length}/60", color = Color.Gray)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = AccentColor
            )
        )
    }
}
@Composable
fun AcceptSinglePay(vm: AppViewModel) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Checkbox(
            checked = vm.inputAcceptSinglePay,
            onCheckedChange = { vm.inputAcceptSinglePay = it },
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryColor
            )
        )

        Text(
            "Aceptar solamente un pago",
            color = SecondColor
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpirationSelector(vm: AppViewModel) {
    val expirationOptions = mapOf(
        "1" to "Vence en 1 día",
        "7" to "Vence en 1 Semana",
        "30" to "Vence en 1 Mes",
        "365" to "Vence en 1 año"
    )
    var expanded by remember { mutableStateOf(false) }

    Column {

        Text(
            "Fecha de vencimiento",
            color = Color.Gray,
            fontSize = 14.sp
        )

        ExposedDropdownMenuBox (
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = expirationOptions[vm.inputQrExpiration] ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu (
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                expirationOptions.forEach { (key, value) ->

                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            vm.inputQrExpiration = key
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun NumberPad(
    onNumber: (Int) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit
) {

    val numbers = listOf(
        listOf("1","2","3"),
        listOf("4","5","6"),
        listOf("7","8","9"),
        listOf(".","0","×")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        numbers.forEach { row ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                row.forEach { key ->

                    KeyButton(
                        text = key,
                        modifier = Modifier.weight(1f)
                    ) {

                        when(key){
                            "×" -> onDelete()
                            "." -> onEnter()
                            else -> onNumber(key.toInt())
                        }

                    }
                }
            }
        }
    }
}
@Composable
fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEDEDED))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            fontSize = 20.sp,
            color = SecondColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
fun ConfirmButton(onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PurpleTop,
            contentColor = WhiteText
        )
    ) {

        Text("Generar QR")
    }
}
@Composable
fun BalanceHeader(vm: AppViewModel) {


    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Bs ",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            color = SecondColor
        )
        Text(
            text = vm.inputQrAmount,
            fontSize = 36.sp,
            color = vm.colorInputQrAmount,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun MainScreen(
    vm: AppViewModel = viewModel()
) {
    QrPaymentScreen(vm)
}
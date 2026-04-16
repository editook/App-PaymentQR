package com.editoapp.qrpagos.ui.theme

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.editoapp.qrpagos.R
import com.editoapp.qrpagos.data.CashierStat
import com.editoapp.qrpagos.data.InfoData
import com.editoapp.qrpagos.data.MonthlyPayment
import com.editoapp.qrpagos.ui.theme.utils.CardPaymentHistory
import com.editoapp.qrpagos.ui.theme.utils.Help
import com.editoapp.qrpagos.viewmodel.AppViewModel



fun demoMonthlyData() = listOf(
    MonthlyPayment("Ene", 1200.00),
    MonthlyPayment("Feb", 1850.00),
    MonthlyPayment("Mar", 900.00),
    MonthlyPayment("Abr", 2100.00),
    MonthlyPayment("May", 2600.00),
    MonthlyPayment("Jun", 1950.00),
    MonthlyPayment("Jul", 1950.00),
    MonthlyPayment("Ago", 1950.00),
    MonthlyPayment("Sep", 1950.00),
    MonthlyPayment("Oct", 1950.00),
    MonthlyPayment("Nov", 1950.00),
    MonthlyPayment("Dic", 1950.00)
)

fun demoCashiers() = listOf(
    CashierStat("Caja 1", listOf(200f, 500f, 300f, 700f, 900f, 650f)),
    CashierStat("Caja 2", listOf(400f, 300f, 800f, 600f, 1200f, 1000f)),
    CashierStat("Caja 3", listOf(150f, 450f, 500f, 400f, 750f, 900f))
)
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun MainHomePreview() {
    val vm  = AppViewModel(LocalContext.current)
    vm.infoData = InfoData(2000.00,"Jose Martinez Seballo","Jose Martinez",listOf(),listOf(),"12345678")
    vm.infoData.MonthPayments = demoMonthlyData()
    MainHome(vm)
}
@Composable
fun MainHome(vm: AppViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {

            item { ModernHeader(vm) }

            item { Spacer(Modifier.height(24.dp)) }

            item { StatsSection(vm) }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Transacciones", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    IconButton(onClick = {vm.currentView = "history"}) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "refrescar",
                            tint = BlackText
                        )
                    }
                }
            }

            if (vm.infoData.payments.isEmpty()) {

                item {
                    Text("No hay registros")
                }

            } else {

                items(vm.infoData.payments.take(5), key = { it.id }) { payment ->
                    CardPaymentHistory(payment, false)
                }

            }

            item { Spacer(Modifier.height(50.dp)) }
        }

    }
}
@Composable
fun ModernHeader(vm: AppViewModel) {

    val gradient = Brush.linearGradient(
        colors = listOf(
            PurpleTop,
            Color(0xFF9D50FF)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Text(
            "Pagos QR",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .background(gradient)
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column{
                            Text(
                                "CUENTA DESTINO",
                                color = Color.White.copy(.7f),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                vm.infoData.AccountNumber,
                                color = Color.White,
                                letterSpacing = 2.sp,
                                fontSize = 18.sp
                            )
                        }
                        IconButton(onClick = { vm.changeShowBalance() }) {

                            Icon(
                                painter = painterResource(if (vm.user.showBalance)
                                    R.drawable.visibility
                                else
                                    R.drawable.visibilityoff),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                "BALANCE DEL DIA",
                                color = Color.White.copy(.7f),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            AnimatedContent(targetState = vm.user.showBalance, label = "") { visible ->

                                Text(
                                    text = if (visible) vm.infoData.getBalance() else "Bs.••••••",
                                    color = Color.White,
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }


                    /// BOTTOM ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {

                        Column(horizontalAlignment = Alignment.End,
                            modifier = Modifier.wrapContentWidth()) {
                            Text(
                                "ACCESO",
                                color = Color.White.copy(.7f),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                vm.infoData.username.toUpperCase(),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    }
                }
            }
        }
    }
}
@Composable
fun StatsSection(vm: AppViewModel) {

    val monthly = vm.infoData.MonthPayments
    val isEmply = vm.isEmplyMonthPayemnts()
    val months = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun","Jul","Ago","Sep","Oct","Nov","Dic")
    val values = monthly.map { it.amount }
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Estadísticas", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            IconButton(onClick = {

            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "refrescar",
                    tint = BlackText
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        if(isEmply){
            Text("No hay registros")
        }
        else{
            ChartCard("Ingresos totales") {
                BarChart(values, months)
            }
        }

        /*Spacer(Modifier.height(16.dp))

        ChartCard("Rendimiento por cajero") {
            MultiLineChart(cashiers)
        }*/
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = ScreenBg)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(title, fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(12.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier.width(500.dp)
                ) {
                    content()
                }
            }
        }
    }
}
@Composable
fun BarChart(data: List<Double>, months: List<String>) {

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {

        val leftPadding = 90f
        val bottomPadding = 60f
        val topPadding = 20f

        val chartWidth = size.width - leftPadding
        val chartHeight = size.height - bottomPadding

        val max = data.maxOrNull() ?: 0.00
        val barWidth = chartWidth / data.size

        val steps = 5

        /// 🔹 EJE Y
        for (i in 0..steps) {

            val value: Double = max / steps * i
            val amountformat = Help.FormatAmount(value)
            val y = chartHeight - (value / max) * chartHeight + topPadding

            drawContext.canvas.nativeCanvas.drawText(
                amountformat,
                0f,
                y.toFloat(),
                android.graphics.Paint().apply {
                    textSize = 26f
                    color = BlackText.toArgb()
                }
            )

            drawLine(
                color = PurpleBottom,
                start = Offset(leftPadding, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }

        /// 🔹 BARRAS
        data.forEachIndexed { i, value ->

            val barHeight = (value / max) * chartHeight

            drawRoundRect(
                color = PurpleTop,
                topLeft = Offset(
                    leftPadding + barWidth * i,
                    (chartHeight - barHeight + topPadding).toFloat()
                ),
                size = Size(barWidth * 0.4f, barHeight.toFloat()),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }

        /// 🔹 EJE X
        months.forEachIndexed { i, month ->

            val x = leftPadding + barWidth * i

            drawContext.canvas.nativeCanvas.drawText(
                month,
                x,
                size.height,
                android.graphics.Paint().apply {
                    textSize = 26f
                    color = BlackText.toArgb()
                }
            )
        }
    }
}
@Composable
fun MultiLineChart(
    cashiers: List<CashierStat>,
    months: List<String> = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun")
) {

    val colors = listOf(
        Color(0xFF7B61FF),
        Color(0xFF00C2FF),
        Color(0xFFFF6D00)
    )

    Column {

        /// 🔹 LEYENDA
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            cashiers.forEachIndexed { index, cashier ->
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colors[index], CircleShape)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(cashier.name, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {

            val leftPadding = 90f
            val bottomPadding = 60f
            val topPadding = 20f

            val chartWidth = size.width - leftPadding
            val chartHeight = size.height - bottomPadding

            val allValues = cashiers.flatMap { it.monthly }
            val max = allValues.maxOrNull() ?: 0f

            val stepX = chartWidth / (months.size - 1)

            /// 🔹 EJE Y (MONTOS)
            val steps = 5

            for (i in 0..steps) {

                val value = max / steps * i
                val y = chartHeight - (value / max) * chartHeight + topPadding

                drawContext.canvas.nativeCanvas.drawText(
                    "Bs ${value.toInt()}",
                    0f,
                    y,
                    android.graphics.Paint().apply {
                        textSize = 26f
                        color = android.graphics.Color.GRAY
                    }
                )

                drawLine(
                    color = Color.LightGray,
                    start = Offset(leftPadding, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            /// 🔹 EJE X (MESES)
            months.forEachIndexed { i, month ->

                val x = leftPadding + stepX * i

                drawContext.canvas.nativeCanvas.drawText(
                    month,
                    x - 20f,
                    size.height,
                    android.graphics.Paint().apply {
                        textSize = 26f
                        color = android.graphics.Color.GRAY
                    }
                )
            }

            /// 🔹 LÍNEAS DE CAJEROS
            cashiers.forEachIndexed { index, cashier ->

                val points = cashier.monthly.mapIndexed { i, value ->

                    Offset(
                        x = leftPadding + stepX * i,
                        y = chartHeight - (value / max) * chartHeight + topPadding
                    )
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = colors[index],
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 5f,
                        cap = StrokeCap.Round
                    )
                }

                /// 🔹 PUNTOS
                points.forEach {
                    drawCircle(
                        color = colors[index],
                        radius = 6f,
                        center = it
                    )
                }
            }
        }
    }
}



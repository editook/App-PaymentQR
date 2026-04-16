package com.editoapp.qrpagos.ui.theme

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.editoapp.qrpagos.viewmodel.AppViewModel
import com.editoapp.qrpagos.R
import com.editoapp.qrpagos.ui.theme.utils.CardPaymentHistory
import com.editoapp.qrpagos.ui.theme.utils.LoadingDataView
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    val context = LocalContext.current
    val vm  = AppViewModel(LocalContext.current)
    HistoryScreen(vm)
}
@Composable
fun DateFilter2(vm: AppViewModel) {

    var tempDate by remember { mutableStateOf(vm.addDays(0)) }
    var showDialog by remember { mutableStateOf(false) }
    Text(
        text = vm.inputDateHistory,
        fontSize = 16.sp,
        color = SecondaryTextColor,
        modifier = Modifier
            .clickable {
                showDialog = true
            }
    )
    if(showDialog){
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {

                Button (
                    onClick = {

                        vm.setDate(tempDate)
                        showDialog = false
                    }
                ) {
                    Text("Filtrar")
                }
            },
            dismissButton = {
                TextButton (
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            },
            title = {
                Text("Seleccionar fecha")
            },
            text = {

                Column {

                    OutlinedTextField(
                        value = tempDate,
                        onValueChange = {
                            tempDate = it
                        },
                        label = { Text("Fecha (yyyy-MM-dd)") },
                        singleLine = true
                    )
                }
            }
        )
    }

}
@Composable
fun DateFilter(vm: AppViewModel) {

    var showDialog by remember { mutableStateOf(false) }

    // valores iniciales
    val today = vm.inputDateHistory.split("-")
    var year by remember { mutableStateOf(today.getOrNull(0) ?: "2026") }
    var month by remember { mutableStateOf(today.getOrNull(1) ?: "01") }
    var day by remember { mutableStateOf(today.getOrNull(2) ?: "01") }

    // dropdown states
    var expandedYear by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }

    val years = (2020..2030).map { it.toString() }
    val months = (1..12).map { it.toString().padStart(2, '0') }
    val days = (1..31).map { it.toString().padStart(2, '0') }

    // botón principal (fecha actual)
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = vm.inputDateHistory,
            fontSize = 16.sp,
            color = SecondaryTextColor
        )
    }

    if (showDialog) {

        AlertDialog(
            onDismissRequest = { showDialog = false },

            confirmButton = {
                Button(
                    onClick = {
                        val formatted = "$year-$month-$day"
                        vm.setDate(formatted)
                        showDialog = false
                    }
                ) {
                    Text("Filtrar")
                }
            },

            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },

            title = { Text("Seleccionar fecha") },

            text = {

                Column {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {

                        // 🔹 Año
                        DropdownField(
                            label = "Año",
                            value = year,
                            expanded = expandedYear,
                            onExpand = { expandedYear = !expandedYear },
                            onDismiss = { expandedYear = false },
                            options = years,
                            onSelect = {
                                year = it
                                expandedYear = false
                            },
                            modifier = Modifier.weight(1.2f)
                        )

                        // 🔹 Mes
                        DropdownField(
                            label = "Mes",
                            value = month,
                            expanded = expandedMonth,
                            onExpand = { expandedMonth = !expandedMonth },
                            onDismiss = { expandedMonth = false },
                            options = months,
                            onSelect = {
                                month = it
                                expandedMonth = false
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // 🔹 Día
                        DropdownField(
                            label = "Día",
                            value = day,
                            expanded = expandedDay,
                            onExpand = { expandedDay = !expandedDay },
                            onDismiss = { expandedDay = false },
                            options = days,
                            onSelect = {
                                day = it
                                expandedDay = false
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpand() },
        modifier = modifier
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismiss() }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = { onSelect(it) }
                )
            }
        }
    }
}
@Composable
fun HistoryScreen(vm: AppViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Historial",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )
            DateFilter(vm)
            IconButton(onClick = {
                vm.getPayments(true)
            }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "refrescar",
                    tint = PurpleTop,
                )
            }

        }
        LoadingDataView(vm)
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(vm.payments) { payment ->
                CardPaymentHistory(payment,true)
            }
        }

    }
}

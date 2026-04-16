package com.editoapp.qrpagos.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.editoapp.qrpagos.R
import com.editoapp.qrpagos.data.StoredSession
import com.editoapp.qrpagos.ui.theme.utils.CreateAlertBox
import com.editoapp.qrpagos.ui.theme.utils.LoadingDataView
import com.editoapp.qrpagos.viewmodel.AppViewModel
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun CreatePosScreenPreview() {
    val context = LocalContext.current
    val vm = AppViewModel(context)
    CreatePosScreen(vm)
}
@Composable
fun CreatePosScreen(vm: AppViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var editingPos by remember { mutableStateOf<StoredSession>(StoredSession()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var posToDelete by remember { mutableStateOf<StoredSession>(StoredSession()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if(vm.pointsOfSale.size< vm.user.maxUsers){
                        editingPos = StoredSession()
                        vm.inputFullName = ""
                        vm.inputCodePost = ""
                        showDialog = true
                    }

                },
                containerColor = if(vm.pointsOfSale.size>= vm.user.maxUsers){PurpleTopLow}else{PrimaryColor},

            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear POS", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Puntos de venta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )

                IconButton(onClick = {
                    vm.getPosts()
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "refrescar",
                        tint = PrimaryColor,
                    )
                }
            }
            LoadingDataView(vm)

            Spacer(Modifier.height(16.dp))
            LazyColumn() {
                items(vm.pointsOfSale) { pos ->
                    PosListItem(
                        pos = pos,
                        onEdit = {
                            editingPos = it
                            vm.inputFullName = it.userName
                            showDialog = true
                        },
                        onDelete = {
                            posToDelete = it
                            showDeleteDialog = true
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }

            }
        }

    }

    if (showDialog) {
        PosEditorDialog(
            vm = vm,
            editingPos = editingPos,
            onDismiss = { showDialog = false },
            onConfirm = {
                if (editingPos.id.isEmpty()) {
                    vm.createPos()
                } else {
                    vm.updatePos(editingPos!!.id, vm.inputFullName)
                }
                showDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            pos = posToDelete,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                vm.deletePos(posToDelete!!.id)
                showDeleteDialog = false
            }
        )
    }
    CreateAlertBox(vm)
}


@Composable
fun PosListItem(pos: StoredSession, onEdit: (StoredSession) -> Unit, onDelete: (StoredSession) -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(ScreenBg.toArgb())
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(pos.userName, fontWeight = FontWeight.Bold,fontSize = 16.sp,modifier = Modifier.padding(bottom = 10.dp))
                Text(pos.getDevice(), color = Color.Gray,fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("ID: ${pos.getKeySecret()}", fontWeight = FontWeight.Bold,modifier = Modifier.padding(bottom = 10.dp))
                    Text("Ult: 03/02 12:45", color = Color.Gray,fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End,modifier = Modifier.padding(start = 5.dp)) {
                    IconButton(modifier = Modifier.size(20.dp),onClick = { onEdit(pos) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = WarningDarkColor)
                    }

                    IconButton(modifier = Modifier.size(20.dp),onClick = { onDelete(pos) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ErrorColor)
                    }
                }


            }
        }

    }

}

@Composable
fun PosEditorDialog(
    vm: AppViewModel,
    editingPos: StoredSession,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = WarningLightColor
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    if (editingPos.userName.isEmpty()) "Nuevo" else "Modificar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = vm.inputFullName,
                    onValueChange = { vm.inputFullName = it },
                    label = { Text("Usuario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryColor,
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor,
                        focusedLabelColor = SecondaryTextColor,
                        unfocusedLabelColor = SecondaryTextColor,
                        focusedContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor,
                    )
                )
                OutlinedTextField(
                    value = if(editingPos.getKeySecret().isEmpty()) vm.inputCodePost else editingPos.posId,
                    onValueChange = { vm.inputCodePost = it },
                    label = { Text("Clave") },
                    enabled =  if(editingPos.getKeySecret().isEmpty()) true else false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryColor,
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor,
                        focusedLabelColor = SecondaryTextColor,
                        unfocusedLabelColor = SecondaryTextColor,
                        focusedContainerColor = SurfaceColor,
                        disabledContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor,
                    )
                )

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("Cancelar", color = PrimaryColor)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessColor)
                    ) {
                        Text(if (editingPos.userName.isEmpty()) "Crear" else "Actualizar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    pos: StoredSession?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (pos == null) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BackgroundColor
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Confirmar Eliminación",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "¿Estás seguro de que deseas eliminar a ${pos.userName}?",
                    color = TextColor
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("No", color = PrimaryColor)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                    ) {
                        Text("Si", color = Color.White)
                    }
                }
            }
        }
    }
}

package com.editoapp.qrpagos.ui.theme

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.editoapp.qrpagos.R
import com.editoapp.qrpagos.viewmodel.AppViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val context = LocalContext.current
    val vm = AppViewModel(context)
    LoginScreen(vm)
}
@Composable
fun GradientLoginBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryColor,
                        PrimaryColor
                    )
                )
            )
    ) {
        content()

    }
}
@Composable
fun LoadingScreenInit() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleTop),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cargando...",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}
@Composable
fun LoginScreen(vm: AppViewModel) {

    if (vm.showDialogInit) {
        LoadingScreenInit()
    }
    else{
        GradientLoginBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
                Spacer(Modifier.height(32.dp))
                Text("Pago QR",modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,fontSize = 32.sp, fontWeight = FontWeight.Bold, color = WhiteText)

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = vm.inputLogin,
                    onValueChange = { vm.inputLogin = it },
                    enabled = !vm.isLoading,
                    label = { Text("Clave de acceso") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = WhiteText,
                        unfocusedIndicatorColor = SecondaryTextColor,
                        cursorColor = BlackText,
                        focusedTextColor = TextColor,
                        unfocusedTextColor = TextColor,
                        focusedLabelColor = BlackText,
                        unfocusedLabelColor = BlackText,
                        focusedContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor,
                        disabledContainerColor = SurfaceColor,
                        errorContainerColor = SurfaceColor
                    )
                )

                if (vm.messageError.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(vm.messageError, color = WhiteText)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { vm.login() },
                    enabled = !vm.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB51219))
                ) {
                    if (vm.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("ACCEDER", color = Color.White)
                    }
                }
            }
        }
    }
}

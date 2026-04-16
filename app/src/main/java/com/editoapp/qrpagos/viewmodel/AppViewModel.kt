package com.editoapp.qrpagos.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.editoapp.qrpagos.cache.AuthDataStore
import kotlinx.coroutines.launch
import com.editoapp.qrpagos.services.AuthService
import com.editoapp.qrpagos.services.PaymentService
import com.editoapp.qrpagos.services.QrService
import kotlinx.coroutines.flow.firstOrNull
import android.graphics.BitmapFactory
import android.util.Base64
import com.editoapp.qrpagos.data.ResponseQR
import com.editoapp.qrpagos.data.UserType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.core.content.FileProvider
import com.editoapp.qrpagos.data.InfoData
import com.editoapp.qrpagos.data.MessageType
import com.editoapp.qrpagos.data.Payment
import com.editoapp.qrpagos.data.QrStatus
import com.editoapp.qrpagos.data.StoredQR
import com.editoapp.qrpagos.data.StoredSession
import com.editoapp.qrpagos.ui.theme.ErrorColor
import com.editoapp.qrpagos.ui.theme.SecondColor
import com.editoapp.qrpagos.ui.theme.utils.Help
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import java.io.File
import java.io.FileOutputStream
class AppViewModel(context: Context
) : ViewModel() {
    var showQrDialog by mutableStateOf(false)

    private val paymentService = PaymentService()
    private val contextTo = context
    private val authDataStore = AuthDataStore(context)
    private val authService = AuthService(authDataStore)
    private val qrService = QrService()

    var currentView by mutableStateOf("login")
    var isLoading by mutableStateOf(false)

    var progressHistory by mutableStateOf(0f)
        private set
    //inputs forms
    var inputDateHistory by mutableStateOf(addDays(0))
        private set
    var inputLogin by mutableStateOf("")
    var inputFullName by mutableStateOf("")
    var inputCodePost by mutableStateOf("")

    var inputQrAmount by mutableStateOf("0.00")
    var inputQrNote by mutableStateOf("")
    var inputQrExpiration by mutableStateOf("7")
    var qrCurrency by mutableStateOf("Bs")
    var inputAcceptSinglePay by mutableStateOf(true)
    //messages
    var messageError by mutableStateOf("")
    var colorInputQrAmount by mutableStateOf(SecondColor)
    //data load values
    var pointsOfSale by mutableStateOf<List<StoredSession>>(emptyList())
    val user: StoredSession
        get() = authDataStore.loggedInUser
    //QR EN EL MAIN
    var dateqrfinish: String by mutableStateOf("xx/xx")
    var amountqr: String by mutableStateOf("0.00")
    var qrbase64:String by mutableStateOf("")
    var noteqr:String by mutableStateOf("")


    //QR NUEVO GENERADO
    var isEnabledModal by mutableStateOf(true)
    var qrExpirationModal by mutableStateOf("")
    var amountQRModal by mutableStateOf("")
    var noteQRModal by mutableStateOf("")
    var qrStatus  by mutableStateOf(QrStatus.NONE)
    var idstatusQr by mutableStateOf("")
    var qrbase64Modal by mutableStateOf("")

    var payments by mutableStateOf<List<Payment>>(emptyList())
    var infoData by mutableStateOf(InfoData())
    var isVerifyEnabled by mutableStateOf(true)
        private set

    var tokenSeconds by mutableStateOf(0)
        private set
    private var tokenJob: Job? = null
    //alert message
    var showDialog by  mutableStateOf(false)
    var showDialogInit by  mutableStateOf(true)
    var dialogType by  mutableStateOf(MessageType.INFO)
    var dialogMessage by mutableStateOf("")

    init {
        viewModelScope.launch {
            delay(3000)
            showDialogInit = false
        }
        viewModelScope.launch {
            authDataStore.loadSession()
            val auto = authService.tryAutoLogin()
            if (auto) {
                loadInformation()
            }
            showDialogInit = false
        }
    }
    fun login() {
        viewModelScope.launch {
            isLoading = true
            messageError = ""
            val success = authService.login(inputLogin)
            inputLogin = ""
            if (success) {
                loadInformation()
            } else {
                messageError = "Clave incorrecta o error de red"
            }
            isLoading = false
        }
    }
    suspend fun loadInformation(){
        openInitView()
        infoLocal()
        loadInfoData()
        loadPayments(false)
        loadPosts()
        resetQrGenerate()
        startTokenTimer()
        if(infoData.username==""){
            showMessage("Usuario no encontrado")
        }
    }

    private fun loadInfoData() {
        viewModelScope.launch {
            infoData = authService.getInfoData(user.getKeySecret(),getDeviceName())
        }
    }

    fun openInitView(){
        currentView = if (user.type == UserType.BRANCH) {
            "main"
        } else{
            "generate"
        }

    }
    fun logout() {
        viewModelScope.launch {
            authService.logout()
            inputLogin = ""
            currentView = "login"
        }
    }
    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun infoLocal() {
        val data = authDataStore.loadQR()
        if (!data.isEmpty()) {
            dateqrfinish = data.date
            amountqr = data.amount
            qrbase64 = data.qr
            noteqr = data.note
        } else {
            amountqr = "0.00"
            dateqrfinish = "xx/xx"
            qrbase64 = ""
            noteqr = ""
        }
    }
    fun isValidCode(value: String): Boolean {
        return value.length >= 6 && value.all { it.isDigit() }
    }
    fun createPos() {
        if(pointsOfSale.size>=user.maxUsers){
            showAlert("Limite de usuarios alcanzado",MessageType.WARNING)
            return
        }
        if(inputCodePost.isEmpty()){
            showMessage("Campo Clave vacío")
            return
        }
        if(inputFullName.isEmpty()){
            showMessage("Campo Usuario vacío")
            return
        }
        if(!isValidCode(inputCodePost)){
            showMessage("Campo Clave vacío o incorrecto")
            return
        }
        if(user.type != UserType.BRANCH){
            showMessage("No tienes permisos para crear")
            return
        }
        viewModelScope.launch {
            val response = paymentService.createPost(inputFullName, inputCodePost,user.majorId)
            var message = "Registrado exitosamente"
            if(!response){
                inputFullName = ""
                inputCodePost = ""
                message = "Fallo al registrar"
            }
            getPosts()
            showMessage(message)
        }

    }
    fun showMessage(text:String){
        Toast.makeText(
            contextTo,
            text,
            Toast.LENGTH_LONG
        ).show()
    }
    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL

        return "${manufacturer.capitalize()} $model"
    }
    fun updatePos(id: String, newUsername: String) {
        if(user.type != UserType.BRANCH){
            showMessage("No tienes permisos para eliminar")
            return
        }
        viewModelScope.launch {
            val response = paymentService.updatePost(id,newUsername,user.getKeySecret())
            var message = "Se Actualizo correctamente"
            if(!response){
                inputFullName = ""
                message = "Fallo al actualizar"
            }
            getPosts()
            showMessage(message)
        }
    }

    fun deletePos(id: String) {
        if(user.type != UserType.BRANCH){
            showMessage("No tienes permisos para eliminar")
            return
        }
        viewModelScope.launch {
            val response = paymentService.deletePost(id,user.posId.toString())
            var message = "Se elimino exitosamente"
            if(!response){
                message = "Fallo al eliminar"
            }
            getPosts()
            showMessage(message)
        }

    }
    fun addDays(days: Long): String {
        val today = LocalDate.now()
        val newDate = today.plusDays(days)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return newDate.format(formatter)
    }
    fun formatDateCustom(date: String): String {

        val months = listOf(
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
        )

        val localDate = LocalDate.parse(date)

        val day = localDate.dayOfMonth
        val month = months[localDate.monthValue - 1]
        val year = localDate.year

        return "$day/$month/$year"
    }
    fun generateQr() {
        val amount = inputQrAmount.toDoubleOrNull() ?: -1.0
        if(amount == -1.0){
            showMessage("Monto ingreso fue incorrecto")
            return
        }
        val singleUse = inputAcceptSinglePay
        val expirationDate = addDays(inputQrExpiration.toLong())

        var gloss = inputQrNote
        if(user.type == UserType.POS){
            gloss = "P"+user.posId+" "+gloss
        }
        else{
            gloss = "P"+user.majorId+" "+gloss
        }
        val brandId = user.getKeySecret()
        viewModelScope.launch {

            val response: ResponseQR = qrService.generateQR(amount, singleUse, expirationDate, gloss,brandId)
            if(!response.isEmpty()){
                isEnabledModal = true
                qrExpirationModal = formatDateCustom(expirationDate)
                qrbase64Modal = response.qr
                amountQRModal = Help.FormatAmount(amount)
                noteQRModal = gloss
                idstatusQr = response.id
                qrStatus = QrStatus.NONE
                currentView = "qrview"
            }else{
                showMessage("Token expirado")
            }
        }


    }
    fun setDate(date: String) {
        if(isValidDate(date)){
            inputDateHistory = date
            getPayments(true)
        }
        else{
            inputDateHistory = addDays(0)
            showMessage("Fecha incorrecta")
        }

    }
    fun isValidDate(date: String): Boolean {
        return try {
            LocalDate.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }
    fun resetQrGenerate() {
        inputQrAmount = "0.00"
        idstatusQr = ""
        inputQrExpiration = "7"
        inputQrNote = "Servicios"
        inputAcceptSinglePay = true
    }

    fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            val file = File(context.cacheDir, "qr_image.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Compartir QR"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendUserToHome() {
        if(isEnabledModal){
            viewModelScope.launch {
                authDataStore.saveQR(qrExpirationModal,qrbase64Modal,inputQrAmount,inputQrNote)
                infoLocal()
                showMessage("QR guardado")
            }
        }
        resetQrGenerate()

        currentView = "generate"
    }
    private fun launchLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            progressHistory = 0f
            val progressJob = launch {
                while (progressHistory < 0.9f) {
                    progressHistory += 0.05f
                    delay(100)
                }
            }
            block()
            progressJob.cancel()
            progressHistory = 1f
            isLoading = false
        }
    }
    fun getPosts(){
        launchLoading {
            loadPosts()
        }
    }
    private suspend fun loadPosts() {
        pointsOfSale = emptyList()
        if (user.type != UserType.POS) {
            pointsOfSale = paymentService.getPosts(user.majorId)
        }
    }

    fun getPayments(sincronize: Boolean) {
        launchLoading {
            loadPayments(sincronize)
        }
    }
    private suspend fun loadPayments(sincronize: Boolean) {
        val brandId = user.getKeySecret()
        val dateHistory = inputDateHistory.ifEmpty { addDays(0) }
        payments = paymentService.paymentsHistory(
            brandId,
            dateHistory,
            sincronize
        )
    }
    fun checkStatusQr() {
        qrStatus = QrStatus.NONE
        var responseAlert =  MessageType.INFO
        isVerifyEnabled = false
        viewModelScope.launch {
            if(idstatusQr!=""){
                val brandId = user.getKeySecret()
                val response  = qrService.statusQr(idstatusQr,brandId)
                if(response !=null){
                    if(response.statusId == "2"){
                        qrStatus = QrStatus.SUCCESS
                        responseAlert = MessageType.INFO
                    }
                    else if(response.statusId == "3" || response.statusId == "4"){
                        qrStatus = QrStatus.ERROR
                        responseAlert = MessageType.ERROR
                    }
                    else{
                        qrStatus = QrStatus.PENDING
                        responseAlert = MessageType.WARNING
                    }
                    val text = when (responseAlert) {
                        MessageType.INFO -> "Pago realizado con exito"
                        MessageType.ERROR -> "Error en la transaccion, intente mas tarde"
                        MessageType.WARNING -> "Pendiente de pago"
                    }
                    getPayments(true)
                    loadInfoData()
                    showAlert(text, responseAlert)
                }
                isVerifyEnabled = true
            }

        }
    }
    fun startTokenTimer() {
        tokenJob?.cancel()

        tokenJob = viewModelScope.launch {
            while (isActive) {
                tokenSeconds = 60

                while (tokenSeconds > 0) {
                    delay(1000)
                    tokenSeconds--
                }
                try {
                    authService.refreshToken(user.majorId)
                } catch (e: Exception) {
                    showMessage("Fallo al generar token, espere un momento y vuelva a intentarlo")
                    delay(1000)
                    authService.refreshToken(user.majorId)
                    tokenSeconds = 60
                }
            }
        }
    }
    fun refreshToken(){
        viewModelScope.launch {
            authService.refreshToken(user.majorId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tokenJob?.cancel()
    }

    fun changeShowBalance() {
        viewModelScope.launch {
            val updatedUser = user.copy(
                showBalance = !user.showBalance
            )
            authDataStore.saveLoginKey(updatedUser)
        }
    }

    fun onNumberPressed(it: Int) {
        val digit = it.toString()

        val newValue = when {

            inputQrAmount.isEmpty() -> digit

            inputQrAmount == "0" -> digit

            inputQrAmount == "0.00" -> digit   // 👈 caso faltante

            else -> inputQrAmount + digit
        }

        if (isValidDecimal(newValue)) {
            inputQrAmount = newValue
        }

        validateInputAmount()
    }
    fun isValidDecimal(value: String): Boolean {
        val parts = value.split(".")
        return when {
            parts.size > 2 -> false
            parts.size == 2 && parts[1].length > 2 -> false
            else -> true
        }
    }

    fun onNumberDelete() {
        if (inputQrAmount.isNotEmpty()) {
            inputQrAmount = inputQrAmount.dropLast(1)
        }
        validateInputAmount()
    }

    fun onNumberPoint() {
        if (!inputQrAmount.contains(".")) {

            inputQrAmount = if (inputQrAmount.isEmpty()) {
                "0."
            } else {
                inputQrAmount + "."
            }
        }
        validateInputAmount()
    }
    fun validateInputAmount(): Boolean{
        if (inputQrAmount.isEmpty()) {
            colorInputQrAmount = SecondColor
            return true
        }
        val number = inputQrAmount.toDoubleOrNull()

        val valid =
            !inputQrAmount.endsWith(".") &&
                    number != null &&
                    number > 0
        colorInputQrAmount = if (valid) SecondColor else ErrorColor
        return valid
    }

    fun openModalQR() {
        if(qrbase64.isEmpty() || qrbase64.length < 20){
            return
        }
        isEnabledModal = false
        qrExpirationModal = dateqrfinish
        qrbase64Modal = qrbase64
        amountQRModal =  amountqr
        noteQRModal = noteqr
        idstatusQr = ""
        qrStatus = QrStatus.NONE
        currentView = "qrview"
    }

    fun isEmplyMonthPayemnts():Boolean {

        for (payment in infoData.MonthPayments) {
            if(payment.amount!=0.00){
                return false
            }
        }
        return true
    }
    fun showAlert(message: String, type: MessageType){
        dialogMessage = message
        dialogType = type
        showDialog =true
    }
}
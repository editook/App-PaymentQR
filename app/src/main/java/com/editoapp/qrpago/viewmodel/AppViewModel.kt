package com.editoapp.qrpago.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.editoapp.qrpago.cache.AuthDataStore
import kotlinx.coroutines.launch
import com.editoapp.qrpago.services.AuthService
import com.editoapp.qrpago.services.PaymentService
import com.editoapp.qrpago.services.QrService
import android.graphics.BitmapFactory
import android.util.Base64
import com.editoapp.qrpago.data.UserType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.editoapp.qrpago.data.InfoData
import com.editoapp.qrpago.data.MessageType
import com.editoapp.qrpago.data.Payment
import com.editoapp.qrpago.data.QrStatus
import com.editoapp.qrpago.data.StoredSession
import com.editoapp.qrpago.data.response.ResponseQR
import com.editoapp.qrpago.ui.theme.ErrorColor
import com.editoapp.qrpago.ui.theme.SecondColor
import com.editoapp.qrpago.ui.theme.utils.Help
import com.editoapp.qrpago.utils.FirebaseTokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import java.io.File
import java.io.FileOutputStream
class AppViewModel(context: Context
) : ViewModel() {
    var showQrDialog by mutableStateOf(false)
    //B7:0E:B1:FF:98:55:F0:D8:F8:A5:46:11:BA:E5:A6:B4:4F:70:B8:23:42:12:CF:93:40:D0:F0:FF:58:3B:2B:08
    private val paymentService = PaymentService()
    private val contextTo = context
    private val authDataStore = AuthDataStore(context)
    private val authService = AuthService(authDataStore)
    private val qrService = QrService()

    var currentView by mutableStateOf("login")
    var isLoading by mutableStateOf(false)
    var refreshing by  mutableStateOf(false)
    var showDialogPost by mutableStateOf(false)

    var progressHistory by mutableStateOf(0f)
        private set
    //inputs forms
    var inputDateHistory by mutableStateOf(addDays(0))
        private set
    var inputLogin by mutableStateOf("")
    var inputKeyAcces by mutableStateOf("")
    var inputFullName by mutableStateOf("")
    var inputNumberPost by mutableStateOf("")
    var inputKeyPost by mutableStateOf("")

    var inputQrAmount by mutableStateOf("0.00")
    var inputQrNote by mutableStateOf("")
    var inputQrExpiration by mutableStateOf("7")
    var qrCurrency by mutableStateOf("Bs")
    var inputAcceptSinglePay by mutableStateOf(false)
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

    //testing
    private var backgroundEventJob: Job? = null
    var isAppVisible = true
    fun startEventsBackground() {
        backgroundEventJob?.cancel()

        backgroundEventJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                if (isAppVisible) {
                    AppEvents.triggerRefreshInfoData()
                    AppEvents.triggerRefreshPayments()
                }

            }
        }
    }

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
        viewModelScope.launch {
            AppEvents.refreshInfoData.collect {
                loadInfoData()
            }

        }
        viewModelScope.launch {
            AppEvents.refreshPayments.collect {
                loadPayments()
            }
        }
        startEventsBackground()
    }
    fun login() {
        viewModelScope.launch {
            isLoading = true
            messageError = ""
            val success = authService.login(inputLogin,inputKeyAcces,getDeviceName())

            inputLogin = ""
            inputKeyAcces = ""
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
        loadPayments()
        loadPosts()
        resetQrGenerate()
        if(user.isEmpty() && infoData.username.isEmpty()){
            showMessage("Usuario no encontrado")
        }
    }

    fun loadInfoData() {5
        if(!user.isEmpty()){
            viewModelScope.launch {
                var token = FirebaseTokenManager.getToken()
                var retries = 0
                while (token.isEmpty() && retries < 10) {
                    delay(300)
                    token = FirebaseTokenManager.getToken()
                    retries++
                }
                infoData = authService.getInfoData(user,token)
            }
        }

    }
    fun generateRandomCode(): String {
        val chars = "LMNOP*1234567890ASDFGHJK+"
        return (1..5)
            .map { chars.random() }
            .joinToString("")
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
            inputKeyAcces = ""
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
        return value.length >= 5 && value.all { it.isDigit() }
    }
    fun createPos() {
        if(pointsOfSale.size>=user.maxUsers){
            showAlert("Limite de usuarios alcanzado",MessageType.WARNING)
            return
        }
        if(inputNumberPost.isEmpty()){
            showMessage("Campo Numero vacío")
            return
        }
        if(inputKeyPost.isEmpty()){
            showMessage("Campo Clave de Acceso vacío")
            return
        }
        if(inputFullName.isEmpty()){
            showMessage("Campo Usuario vacío")
            return
        }
        if(!isValidCode(inputNumberPost)){
            showMessage("Campo Numero debe ser numerico y por los menos 5 digitos")
            return
        }
        if(user.type != UserType.BRANCH){
            showMessage("No tienes permisos para crear registros")
            return
        }
        viewModelScope.launch {
            val response = paymentService.createPost(inputFullName, inputNumberPost,inputKeyPost,user)
            if(response.isSuccess()){
                showDialogPost  = false
                inputFullName = ""
                inputNumberPost = ""
                inputKeyPost = ""
                getPosts()
            }

            showMessage(response.message)

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

        return "${manufacturer.uppercase()} $model"
    }
    fun updatePos(id: String) {
        //vm.inputFullName,vm.inputKeyPost
        if(user.type != UserType.BRANCH){
            showMessage("No tienes permisos para eliminar")
            return
        }
        if(inputFullName.isEmpty()){
            showMessage("Campo Usuario vacío")
            return
        }
        if(inputKeyPost.isEmpty()){
            showMessage("Campo Clave vacío")
            return
        }
        if(inputNumberPost.isEmpty()){
            showMessage("Campo Numero vacío")
            return
        }
        viewModelScope.launch {
            val response = paymentService.updatePost(id,inputFullName,inputNumberPost,inputKeyPost,user)
            if(response.isSuccess()){
                showDialogPost = false
                inputFullName = ""
                inputNumberPost = ""
                inputKeyPost = ""
                getPosts()
            }
            showMessage(response.message)
        }
    }

    fun deletePos(id: String) {
        if(user.type != UserType.BRANCH){
            showMessage("No tienes permisos para eliminar")
            return
        }
        viewModelScope.launch {
            val response = paymentService.deletePost(id,user)
            if(response.isSuccess()){
                getPosts()
            }
            showMessage(response.message)
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

        val gloss = "P"+user.getUserNumber()+" "+inputQrNote

        viewModelScope.launch {

            val response: ResponseQR = qrService.generateQR(amount, singleUse, expirationDate, gloss,user)
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
            getPayments()
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
        inputAcceptSinglePay = false
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

        if (user.type != UserType.POS) {
            val response = paymentService.getPosts(user)
            if(response.isSuccess()){
                response.data?.let { pointsOfSale = it.posts }
            }
            else{
                showMessage(response.message)
            }
        }
    }

    fun getPayments() {
        launchLoading {
            loadPayments()
        }
    }
    private suspend fun loadPayments() {
        val dateHistory = inputDateHistory.ifEmpty { addDays(0) }
        payments = paymentService.paymentsHistory(
            user,
            dateHistory
        )
    }
    fun checkStatusQr() {
        qrStatus = QrStatus.NONE
        var responseAlert =  MessageType.INFO
        isVerifyEnabled = false
        viewModelScope.launch {
            if(idstatusQr!=""){
                val response  = qrService.statusQr(idstatusQr,user)
                if(response !=null){
                    if(response.statusId == "2"){
                        qrStatus = QrStatus.SUCCESS
                        responseAlert = MessageType.SUCCESS
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
                        MessageType.SUCCESS -> "Pago realizado con exito"
                        MessageType.ERROR -> "Error en la transaccion, intente mas tarde"
                        MessageType.WARNING -> "Pendiente de pago"
                        MessageType.INFO -> "Pendiente de pago"
                    }
                    getPayments()
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
            }
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
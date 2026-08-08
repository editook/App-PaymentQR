package com.editoapp.qrpago.data

import com.editoapp.qrpago.ui.theme.utils.Help

data class Payment(
    val id: String,
    val amount: Double,
    val date: String,
    val time: String,
    val note: String,
    val sourceBank: String,
    val originName: String,
    val voucherId:String
)
enum class UserType { POS, BRANCH }

data class StoredSession(
    val id: String = "",
    val posNumber: String = "",
    val majorNumber: String = "",

    var type: UserType = UserType.BRANCH,
    val source : String = "",
    val userName: String = "",
    var showBalance: Boolean = false,
    val usedDevice: String = "",
    val keyAccess: String = "",
    val lastConnect: String = "",
    val lvlPlan: Int = 1,
    val maxUsers:Int = 0
) {
    fun getUserNumber() :String{
        if(type == UserType.BRANCH){
            return majorNumber
        }
        return posNumber
    }
    fun isEmpty(): Boolean{
        if(id.isEmpty()){
            return true
        }
        if(majorNumber.isEmpty()){
            return true
        }
        if(userName.isEmpty()){
            return true
        }
        if(keyAccess.isEmpty()){
            return true
        }
        return false
    }
    fun getDevice():String{
        if(usedDevice.isEmpty()){
            return ""
        }
        if (usedDevice.length <= 20) {
            return usedDevice
        }
        return usedDevice.take(20) + ".."
    }
}
data class MonthlyPayment(
    var month: String,
    val amount: Double
) {
    fun updateMonth() {
        month = when (month) {
            "1" -> "Ene"
            "2" -> "Feb"
            "3" -> "Mar"
            "4" -> "Abr"
            "5" -> "May"
            "6" -> "Jun"
            "7" -> "Jul"
            "8" -> "Ago"
            "9" -> "Sep"
            "10" -> "Oct"
            "11" -> "Nov"
            "12" -> "Dic"
            else -> {"Ene"}
        }
    }
}

data class CashierStat(
    val name: String,
    val monthly: List<Float>
)
data class StoredQR(
    val qr: String ="",
    val date: String ="",
    val amount: String ="",
    val note:String=""
){
    fun isEmpty(): Boolean{
        return qr.isEmpty() || date.isEmpty() || amount.isEmpty()
    }
}


data class InfoData(
    val totalBalance: Double=0.0,
    val fullName: String = "",
    val username: String = "",
    var payments: List<Payment> = listOf(),
    var MonthPayments: List<MonthlyPayment> = listOf(
        MonthlyPayment("Ene", 0.00),
        MonthlyPayment("Feb", 0.00),
        MonthlyPayment("Mar", 0.00),
        MonthlyPayment("Abr", 0.00),
        MonthlyPayment("May", 0.00),
        MonthlyPayment("Jun", 0.00),
        MonthlyPayment("Jul", 0.00),
        MonthlyPayment("Ago", 0.00),
        MonthlyPayment("Sep", 0.00),
        MonthlyPayment("Oct", 0.00),
        MonthlyPayment("Nov", 0.00),
        MonthlyPayment("Dic", 0.00)
    ),
    val AccountNumber: String = "") {

    fun getShortAccountNumber(): String {

        if (AccountNumber.length <= 4) {
            return AccountNumber
        }
        val lastDigits = AccountNumber.takeLast(4)
        val masked = "*".repeat(AccountNumber.length - 4)

        return masked + lastDigits
    }
    fun getBalance(): String {
        return "Bs." + Help.FormatAmount(totalBalance)
    }
}
enum class QrStatus {
    PENDING,
    SUCCESS,
    ERROR,
    NONE
}

enum class MessageType {
    ERROR,
    WARNING,
    INFO,
    SUCCESS
}
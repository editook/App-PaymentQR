package com.editoapp.qrpago.data.response

data class ResponseQR(
    val qr: String="",
    val amount: String="",
    val date: String="",
    val id: String=""
){
    fun isEmpty(): Boolean{
        return qr.isEmpty() || date.isEmpty() || amount.isEmpty()
    }
}
data class ResponseStatusQR(
    val statusId:String?,
    val message:String?
)
data class Response<T>(
    var responseCode: Int = 500,
    val message: String = "Error en la transaccion",
    var data: T? = null
) {
    fun isSuccess() = responseCode == 200
}
data class DataEmply(
    val none:Int = 0
){}
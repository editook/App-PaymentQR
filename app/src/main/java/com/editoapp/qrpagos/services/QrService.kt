package com.editoapp.qrpagos.services

import com.editoapp.qrpagos.data.ResponseQR
import com.editoapp.qrpagos.data.ResponseStatusQR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class QrService {

    suspend fun generateQR(
        amount: Double,
        singleUse: Boolean,
        expirationDate: String,
        gloss: String,
        brandId: String
    ): ResponseQR = withContext(Dispatchers.IO) {

        try {
            val url = URL("https://editoapp.com/service-pagosqr/createqr")
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("brandId", brandId)
                put("gloss", gloss)
                put("singleUse", singleUse)
                put("expirationDate", expirationDate)
                put("amount", amount)
            }.toString()

            conn.outputStream.use {
                it.write(jsonInputString.toByteArray(Charsets.UTF_8))
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)

                return@withContext ResponseQR(
                    qr = responseJson.optString("qr"),
                    amount = amount.toString(),
                    date = expirationDate,
                    id = responseJson.optString("id"),
                )
            }

            return@withContext ResponseQR()

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ResponseQR()
        }
    }

    suspend fun statusQr(idstatusQr: String, brandId: String) : ResponseStatusQR? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://editoapp.com/service-pagosqr/statusqr")
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 5000

                val jsonInputString = JSONObject().apply {
                    put("brandId", brandId)
                    put("id", idstatusQr)
                }.toString()

                conn.outputStream.use {
                    it.write(jsonInputString.toByteArray(Charsets.UTF_8))
                }

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                    val responseJson = JSONObject(responseText)

                    return@withContext ResponseStatusQR(
                        statusId = responseJson.optString("statusId"),
                        message = responseJson.optString("message")
                    )
                }

                null

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
package com.editoapp.qrpagos.services

import com.editoapp.qrpagos.data.Payment
import com.editoapp.qrpagos.data.StoredSession
import com.editoapp.qrpagos.data.UserType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PaymentService {
    suspend fun paymentsHistory(brandId: String, date: String,sincronize:Boolean): List<Payment> =
        withContext(Dispatchers.IO) {

            try {

                val url = URL("https://editoapp.com/service-pagosqr/history")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 5000

                val jsonInputString = JSONObject().apply {
                    put("brandId", brandId)
                    put("date", date)
                    put("sincronize", sincronize)
                }.toString()

                conn.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                if (conn.responseCode == 200) {
                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                    val responseJson = JSONObject(responseText)
                    val paymentsArray = responseJson.optJSONArray("payments")
                        ?: return@withContext emptyList()
                    val paymentsList = mutableListOf<Payment>()
                    for (i in 0 until paymentsArray.length()) {
                        val item = paymentsArray.getJSONObject(i)

                        val payment = Payment(
                            id = item.optString("id"),
                            amount = item.optDouble("amount", 0.0),
                            date = item.optString("date", ""),
                            note = item.optString("note", ""),
                            sourceBank = item.optString("sourceBank", ""),
                            time = item.optString("time", ""),
                            voucherId = item.optString("voucherId", "")
                        )

                        paymentsList.add(payment)
                    }
                    conn.disconnect()
                    return@withContext paymentsList
                }
                return@withContext emptyList()

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }
        }

    suspend fun createPost(username: String, branchId: String, branchIdMaster: String): Boolean =
        withContext(Dispatchers.IO) {
            try {

                val url = URL("https://editoapp.com/service-pagosqr/createpost")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 5000

                val jsonInputString = JSONObject().apply {
                    put("brand", branchIdMaster)
                    put("brandId", branchId)
                    put("username", username)
                }.toString()

                conn.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                if (conn.responseCode == 200) {

                    conn.disconnect()
                    return@withContext true
                }
                return@withContext false

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }

    suspend fun updatePost(id: String,username:String,brandId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {

                val url = URL("https://editoapp.com/service-pagosqr/updatepost")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 5000

                val jsonInputString = JSONObject().apply {
                    put("id", id)
                    put("brandId", brandId)
                    put("username", username)
                }.toString()

                conn.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                if (conn.responseCode == 200) {

                    conn.disconnect()
                    return@withContext true
                }
                return@withContext false

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }

    suspend fun deletePost(id: String,brandId: String): Boolean = withContext(Dispatchers.IO) {
        try {

            val url = URL("https://editoapp.com/service-pagosqr/deletepost")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("id", id)
                put("brandId", brandId)
            }.toString()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (conn.responseCode == 200) {

                conn.disconnect()
                return@withContext true
            }
            return@withContext false

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    suspend fun getPosts(brandId: String): List<StoredSession> = withContext(Dispatchers.IO) {

        try {

            val url = URL("https://editoapp.com/service-pagosqr/posts")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("brandId", brandId)
            }.toString()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)
                val paymentsArray = responseJson.optJSONArray("posts")
                    ?: return@withContext emptyList()
                val responseList = mutableListOf<StoredSession>()
                for (i in 0 until paymentsArray.length()) {
                    val item = paymentsArray.getJSONObject(i)

                    val element = StoredSession(
                        id = item.optString("id"),
                        usedDevice = item.optString("identifier"),
                        userName = item.optString("username"),
                        posId = item.optString("branchId"),
                        majorId = brandId,
                        type = UserType.POS,
                        showBalance = false
                    )

                    responseList.add(element)
                }
                conn.disconnect()
                return@withContext responseList
            }
            return@withContext emptyList()

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}
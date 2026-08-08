package com.editoapp.qrpago.services

import com.editoapp.qrpago.data.Payment
import com.editoapp.qrpago.data.StoredSession
import com.editoapp.qrpago.data.response.DataEmply
import com.editoapp.qrpago.data.response.Response
import com.editoapp.qrpago.utils.JsonParse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PaymentService {
    data class PaymentsResponse(
        val payments: List<Payment>
    )
    suspend fun paymentsHistory(user: StoredSession, date: String): List<Payment> =
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
                    put("number", user.getUserNumber())
                    put("key_access", user.keyAccess)
                    put("date", date)
                }.toString()

                conn.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                if (conn.responseCode == 200) {
                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }

                    val gson = Gson()
                    val response = gson.fromJson(responseText, PaymentsResponse::class.java)

                    val paymentsList = response.payments.map { item ->
                        Payment(
                            id = item.id,
                            amount = item.amount,
                            date = item.date,
                            note = item.note,
                            sourceBank = item.sourceBank,
                            time = item.time,
                            voucherId = item.voucherId,
                            originName = item.originName
                        )
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

    suspend fun createPost(username: String, new_number: String,keyAccess:String, user: StoredSession): Response<DataEmply> =
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
                    put("number", user.getUserNumber())
                    put("key_access", user.keyAccess)

                    put("new_number", new_number)
                    put("username", username)
                    put("new_key_access", keyAccess)

                }.toString()

                conn.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                if (conn.responseCode == 200 || conn.responseCode == 500 || conn.responseCode == 404) {
                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }

                    val response = JsonParse.from<Response<DataEmply>>(responseText)
                    response.responseCode = conn.responseCode
                    conn.disconnect()
                    return@withContext response
                }
                return@withContext Response()

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Response()
            }
        }

    suspend fun updatePost(id: String,username:String,new_number:String,newKeyAccess:String,user: StoredSession): Response<DataEmply> =
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
                    put("number", user.getUserNumber())
                    put("key_access", user.keyAccess)
                    put("new_key_access", newKeyAccess)
                    put("new_number", new_number)
                    put("username", username)

                }.toString()

                conn.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                if (conn.responseCode == 200 || conn.responseCode == 500 || conn.responseCode == 404) {

                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }

                    val response = JsonParse.from<Response<DataEmply>>(responseText)
                    response.responseCode = conn.responseCode
                    conn.disconnect()
                    return@withContext response
                }
                return@withContext Response()

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Response()
            }
        }

    suspend fun deletePost(id: String,user: StoredSession): Response<DataEmply> = withContext(Dispatchers.IO) {
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
                put("number", user.getUserNumber())
                put("key_access", user.keyAccess)
            }.toString()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (conn.responseCode == 200 || conn.responseCode == 500 || conn.responseCode == 404) {

                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val response = JsonParse.from<Response<DataEmply>>(responseText)
                response.responseCode = conn.responseCode
                conn.disconnect()
                return@withContext response
            }
            return@withContext Response()

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Response()
        }
    }
    data class PostsResponse(
        var posts: List<StoredSession> = emptyList()
    )

    suspend fun getPosts(user: StoredSession): Response<PostsResponse> = withContext(Dispatchers.IO) {

        try {

            val url = URL("https://editoapp.com/service-pagosqr/posts")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("number", user.getUserNumber())
                put("key_access", user.keyAccess)
            }.toString()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }
            val response = Response(responseCode = conn.responseCode, message = "Error al optener los datos", data = PostsResponse(posts = emptyList()))
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            if (conn.responseCode == 200) {
                val responsePost = JsonParse.from<PostsResponse>(responseText)
                response.data = responsePost
                return@withContext response
            }
            else if(conn.responseCode == 500 || conn.responseCode == 404){
                val responsePost = JsonParse.from<Response<PostsResponse>>(responseText)

                return@withContext responsePost
            }

            return@withContext Response()

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Response()
        }
    }
}
package com.editoapp.qrpago.services

import com.editoapp.qrpago.cache.AuthDataStore
import com.editoapp.qrpago.data.InfoData
import com.editoapp.qrpago.data.MonthlyPayment
import com.editoapp.qrpago.data.StoredSession
import com.editoapp.qrpago.data.UserType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AuthService(private val authDataStore: AuthDataStore) {

    suspend fun login(key: String, password: String,usedDevice:String): Boolean = withContext(Dispatchers.IO) {
        val number = key.trim()

        try {

            val url = URL("https://editoapp.com/service-pagosqr/login")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("number", number)
                put("key_access", password)
                put("used", usedDevice)
            }.toString()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val gson = Gson()
                var response = gson.fromJson(responseText, StoredSession::class.java)
                var role = UserType.BRANCH
                if (response.source == "accounts") {
                    role = UserType.POS
                }
                response.type = role

                authDataStore.saveLoginKey(response)
                return@withContext true
            }
            return@withContext false

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    suspend fun tryAutoLogin(): Boolean {
        val user = authDataStore.sessionFlow.firstOrNull()

        if (user != null && !user.isEmpty()) {
            return true
        }
        return false
    }

    suspend fun logout() {
        authDataStore.clearLoginKey()
    }

    suspend fun getInfoData(user: StoredSession,tokenDevice:String): InfoData = withContext(Dispatchers.IO){

        try {
            val url = URL("https://editoapp.com/service-pagosqr/getinfo")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("number", user.getUserNumber())
                put("key_access", user.keyAccess)
                put("token_notification",tokenDevice)
            }.toString()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = conn.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val gson = Gson()
                val infoData:InfoData = gson.fromJson(responseText, InfoData::class.java)
                val newList = mutableListOf<MonthlyPayment>()
                for (item in infoData.MonthPayments ){
                    item.updateMonth()
                    newList.add(item)
                }
                infoData.MonthPayments = newList
                return@withContext infoData
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() }
                return@withContext InfoData()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext InfoData()
        }
    }

}
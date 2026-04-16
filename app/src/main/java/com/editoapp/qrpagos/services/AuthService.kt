package com.editoapp.qrpagos.services

import com.editoapp.qrpagos.cache.AuthDataStore
import com.editoapp.qrpagos.data.InfoData
import com.editoapp.qrpagos.data.MonthlyPayment
import com.editoapp.qrpagos.data.StoredSession
import com.editoapp.qrpagos.data.UserType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AuthService(private val authDataStore: AuthDataStore) {

    suspend fun login(key: String): Boolean = withContext(Dispatchers.IO) {
        val clean = key.trim()

        try {

            val url = URL("https://editoapp.com/service-pagosqr/login")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("loginKey", clean)
            }.toString()

            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)

                val id = responseJson.optString("id", "")
                val username = responseJson.optString("username", "")
                val use_device = responseJson.optString("used", "")
                val lvlplan = responseJson.optInt("lvlplan", 1)
                val maxUsers = responseJson.optInt("maxUsers", 0)

                var role = UserType.BRANCH
                if (clean.contains("#")) {
                    role = UserType.POS
                }
                if (responseJson.has("id")) {
                    val user = StoredSession(
                        posId = if (role == UserType.POS) clean.split("#")[0] else "",
                        majorId = if (role == UserType.POS) clean.split("#")[1] else clean,
                        id = id,
                        userName = username,
                        type = role,
                        showBalance = true,
                        usedDevice = use_device,
                        lvlPlan = lvlplan,
                        maxUsers = maxUsers
                    )
                    authDataStore.saveLoginKey(user)

                    return@withContext true
                }
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


    suspend fun refreshToken(brandId: String): Boolean = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null

        try {
            val url = URL("https://editoapp.com/service-pagosqr/cron/index.php?brandId=" + brandId)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                connectTimeout = 5000
                readTimeout = 5000
            }

            val responseCode = conn.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.bufferedReader().use { it.readText() }
                return@withContext true
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() }
                return@withContext false
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun getInfoData(keySecret: String, deviceName: String): InfoData = withContext(Dispatchers.IO){

        try {
            val url = URL("https://editoapp.com/service-pagosqr/getinfo")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000

            val jsonInputString = JSONObject().apply {
                put("brandId", keySecret)
                put("used", deviceName)
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
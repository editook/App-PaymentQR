package com.editoapp.qrpagos.cache

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.editoapp.qrpagos.data.StoredQR
import com.editoapp.qrpagos.data.StoredSession
import com.editoapp.qrpagos.data.UserType
import com.editoapp.qrpagos.ui.theme.utils.Help
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs1")
class AuthDataStore(private val context: Context) {
    var loggedInUser by mutableStateOf(StoredSession())

        private set
    companion object {
        private val LOGIN_POST = stringPreferencesKey("login_post")
        private val LOGIN_MAJOR = stringPreferencesKey("login_branch")
        private val KEY_ID = stringPreferencesKey("key_id")
        private val KEY_QR = stringPreferencesKey("key_qr")
        private val KEY_DATE_QR = stringPreferencesKey("key_date_qr")
        private val KEY_AMOUNT_QR = stringPreferencesKey("key_amount_qr")
        private val KEY_NOTE_QR = stringPreferencesKey("key_note_qr")
        private val KEY_ROLE = stringPreferencesKey("key_role")
        private val KEY_USER = stringPreferencesKey("key_username")
        private val KEY_SHOW_BAlANCE = booleanPreferencesKey("key_show_balance")
        private val KEY_LVL_PLAN = stringPreferencesKey("key_lvl_plan")
        private val KEY_MAX_USERS = stringPreferencesKey("key_max_users")
    }

    suspend fun loadSession() {
        loggedInUser = sessionFlow.firstOrNull() ?: StoredSession()
    }
    val sessionFlow: Flow<StoredSession> = context.dataStore.data
        .map { preferences ->

            val roleStr = preferences[KEY_ROLE]

            val userType = try {
                if (roleStr != null) UserType.valueOf(roleStr)
                else UserType.BRANCH
            } catch (e: Exception) {
                UserType.BRANCH
            }

            StoredSession(
                posId = preferences[LOGIN_POST] ?: "",
                majorId = preferences[LOGIN_MAJOR] ?: "",
                id = preferences[KEY_ID] ?: "",
                type = userType,
                userName = preferences[KEY_USER] ?: "",
                showBalance = preferences[KEY_SHOW_BAlANCE] ?: true,
                lvlPlan = preferences[KEY_LVL_PLAN]?.toIntOrNull() ?: 1,
                maxUsers = preferences[KEY_MAX_USERS]?.toIntOrNull() ?: 0
            )
        }
    val QRFlow: Flow<StoredQR> = context.dataStore.data
        .map { preferences ->
            if (preferences[KEY_QR] == null || preferences[KEY_DATE_QR] == null || preferences[KEY_AMOUNT_QR] == null || preferences[KEY_NOTE_QR] == null ) {
                StoredQR()
            }
            else{
                StoredQR(
                    qr = preferences[KEY_QR]+"",
                    date = preferences[KEY_DATE_QR]+"",
                    amount = preferences[KEY_AMOUNT_QR]+"",
                    note = preferences[KEY_NOTE_QR]+""
                )
            }

        }

    suspend fun loadQR(): StoredQR {
        return QRFlow.firstOrNull() ?: StoredQR()
    }
    suspend fun saveLoginKey(user: StoredSession) {
        context.dataStore.edit { preferences ->
            preferences[LOGIN_POST] = user.posId
            preferences[LOGIN_MAJOR] = user.majorId
            preferences[KEY_ID] = user.id
            preferences[KEY_ROLE] = user.type.toString()
            preferences[KEY_USER] = user.userName
            preferences[KEY_SHOW_BAlANCE] = user.showBalance
            preferences[KEY_LVL_PLAN] = user.lvlPlan.toString()
            preferences[KEY_MAX_USERS] = user.maxUsers.toString()

        }
        loggedInUser = user
    }

    suspend fun clearLoginKey() {
        context.dataStore.edit { preferences ->
            preferences.remove(LOGIN_POST)
            preferences.remove(LOGIN_MAJOR)
            preferences.remove(KEY_ID)
            preferences.remove(KEY_ROLE)
            preferences.remove(KEY_USER)
            preferences.remove(KEY_QR)
            preferences.remove(KEY_SHOW_BAlANCE)
            preferences.remove(KEY_LVL_PLAN)
            preferences.remove(KEY_MAX_USERS)
            preferences.remove(KEY_DATE_QR)
            preferences.remove(KEY_AMOUNT_QR)
        }
        loggedInUser = StoredSession()
    }

    suspend fun saveQR(qrExpirationModal: String, qrbase64: String, qrAmount: String,note:String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DATE_QR] = qrExpirationModal
            preferences[KEY_QR] = qrbase64
            preferences[KEY_AMOUNT_QR] = qrAmount
            preferences[KEY_NOTE_QR] = note
        }
    }
}
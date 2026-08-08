package com.editoapp.qrpago.utils

object FirebaseTokenManager {

    private var token: String? = null

    fun setToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String {
        return token ?: ""
    }

    fun hasToken(): Boolean {
        return !token.isNullOrEmpty()
    }
}
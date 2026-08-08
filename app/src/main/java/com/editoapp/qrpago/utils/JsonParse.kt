package com.editoapp.qrpago.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonParse {

    inline fun <reified T> from(json: String): T {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(json, type)
    }
}
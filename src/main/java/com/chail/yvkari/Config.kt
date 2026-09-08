package com.chail.yvkari

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object Config {
    private lateinit var sp: SharedPreferences

    fun init(context: Context){
        this.sp = context.getSharedPreferences("cfg", Context.MODE_PRIVATE)
    }

    var apiKey: String//Key
        get() = sp.getString("api_key", "") ?:""
        set(value) = sp.edit { putString("api_key", value) }

    var baseUrl: String
        get() = sp.getString("base_url", "") ?:""
        set(value) = sp.edit { putString("base_url", value) }

    var model: String
        get() = sp.getString("model", "") ?:""
        set(value) = sp.edit { putString("model", value) }
}
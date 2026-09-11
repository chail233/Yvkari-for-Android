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

    var recordLimit: Int//对话轮数上限
        get() = sp.getInt("record_limit", 20)
        set(value) = sp.edit {putInt("record_limit", value)}

    var debugMode: Boolean
        get() = sp.getBoolean("debug_mode", false)
        set(value) = sp.edit { putBoolean("debug_mode", value) }

    var tokens: Long
        get() = sp.getLong("tokens", 0)
        set(value) = sp.edit { putLong("tokens", value) }

    var msgCount: Int //ai消息条数
        get() = sp.getInt("msg_count", 0)
        set(value) = sp.edit{ putInt("msg_count", value)}
}
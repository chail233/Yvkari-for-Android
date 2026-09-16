package com.chail.yvkari

import com.google.gson.Gson
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

//  Utilities
fun getTime(time: String): String {
//    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    val date = format.parse(time)
//    date?.let {
//        val calendar = Calendar.getInstance()
//        calendar.time = it
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//        return "$hour:$minute"
//    }
//    return "
    return time
}
fun getFullTime(): String {
    val sdf = SimpleDateFormat("yyyy‑MM‑dd HH:mm:ss", Locale.CHINA)
    val ms = System.currentTimeMillis()
    return sdf.format(Date(ms))
}

fun <T> parse(jsonStr: String, clazz: Class<T>): T? {
    val gson = Gson()
    return try {
        gson.fromJson(jsonStr, clazz)
    } catch (e: Exception) {
        println("解析失败：$e 原始文本=$jsonStr")
        null
    }
}

fun getFullException(e: Throwable) : String{
    when(e) {
        is HttpException -> {
            val body = e.response()?.errorBody()?.string()
            return ("==== HTTP ERROR ${e.code()} ====\n")+ ("response error body: $body\n")+("完整堆栈:\n${e.stackTraceToString()}")
        }
        else -> {
            return ("==== EXCEPTION ====")+("完整堆栈:\n${e.stackTraceToString()}")
        }
    }
}

fun getTimeFromSecondStamp(stamp: Long): String{
    val instant = Instant.ofEpochSecond(stamp)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    return localDateTime.toString()
}
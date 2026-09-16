package com.chail.yvkari.debug

import com.chail.yvkari.getFullTime

data class Log(
    val time: String,
    val content: String
)

object Logger {
    private val data = mutableListOf<Log>()

    fun add(content: String){
        data.add(Log(getFullTime(), content))
    }

    fun getLogs(): List<Log>{
        return data.toList()
    }

}
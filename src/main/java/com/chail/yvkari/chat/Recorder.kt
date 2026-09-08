package com.chail.yvkari.chat

import com.chail.yvkari.Config
import com.chail.yvkari.ConfigDev
import com.chail.yvkari.DEV

data class Record(
    val role: String,
    val content: String
)

object Recorder {
    //全局单例记录聊天历史
    var data: MutableList<Record> = mutableListOf()
    fun push(msg: Record){
        val lim = if(DEV) ConfigDev.recordLimit else Config.recordLimit
        while (data.size>lim) data.removeAt(0)
        data.add(msg)
    }
}
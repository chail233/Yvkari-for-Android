package com.chail.yvkari.chat

data class Record(
    val role: String,
    val content: String
)

object Recorder {
    //全局单例记录聊天历史
    var data: MutableList<Record> = mutableListOf()
    fun push(msg: Record){
        while (data.size>20) data.removeAt(0)
        data.add(msg)
    }
}
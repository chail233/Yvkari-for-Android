package com.chail.yvkari.chat.data

import com.chail.yvkari.Config
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class Record(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")//防止被混淆
    val content: String
)

object Recorder {
    private val data: MutableList<Record> = mutableListOf()
    private val mutex = Mutex()//防止并发操作引发bug

    suspend fun push(msg: Record) {
        mutex.withLock {
            val lim = Config.recordLimit
            while (data.size > lim) data.removeAt(0)
            data.add(msg)
        }
    }

    suspend fun getList(): List<Record> = mutex.withLock {
        data.toList()
    }

    suspend fun clear(){
        mutex.withLock{
            data.clear()
        }
    }
}
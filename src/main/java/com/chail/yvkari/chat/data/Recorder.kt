package com.chail.yvkari.chat.data

import com.chail.yvkari.Config
import com.chail.yvkari.ConfigDev
import com.chail.yvkari.DEV
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class Record(
    val role: String,
    val content: String
)

object Recorder {
    private val data: MutableList<Record> = mutableListOf()
    private val mutex = Mutex()//防止并发操作引发bug

    suspend fun push(msg: Record) {
        mutex.withLock {
            val lim = if (DEV) ConfigDev.recordLimit else Config.recordLimit
            while (data.size > lim) data.removeAt(0)
            data.add(msg)
        }
    }

    suspend fun getList(): List<Record> = mutex.withLock {
        data.toList()
    }
}
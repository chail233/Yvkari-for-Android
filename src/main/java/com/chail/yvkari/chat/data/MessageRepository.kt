package com.chail.yvkari.chat.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class MessageRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).messageDao()

    // 插入消息
    suspend fun insertMessage(msg: Message) {
        dao.insertMessage(msg)
    }

    // 获取消息流，供界面观察
    fun observeAllMessages(): Flow<List<Message>> {
        return dao.getAllMessage()
    }

    suspend fun clearAllMessage() {
        dao.clearAll()
    }
}
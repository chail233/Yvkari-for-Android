package com.chail.yvkari.memory

import com.chail.yvkari.Config
import com.chail.yvkari.chat.data.Recorder
import com.chail.yvkari.debug.Logger
import com.chail.yvkari.getTimeFromSecondStamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun addMemory(){
    val body = AddBody(
        user_id = Config.userId,
        messages = Recorder.getMem(),
    )
    Logger.add("记忆添加请求：$body")
    val res = withContext(Dispatchers.IO){
        memoryApi.AddMemory(body)
    }
    Logger.add("记忆添加响应：$res")
    Config.memCount += res.memory_nodes.size
    Recorder.clearMem()
}

suspend fun queryMemory(): List<String>{
    val body = SearchBody(
        user_id = Config.userId,
        messages = Recorder.getList()
    )
    Logger.add("记忆召回请求：$body")
    val res = withContext(Dispatchers.IO){
        memoryApi.SearchMemory(req = body)
    }
    Logger.add("记忆添召回响应：$res")
    val resStr = mutableListOf<String>()
    if(res.memory_nodes.isNotEmpty()){
        for(it in res.memory_nodes) {
            val localDateTime = getTimeFromSecondStamp(it.updated_at)
            resStr.add("[$localDateTime]${it.content};")
        }
    }
    return resStr
}

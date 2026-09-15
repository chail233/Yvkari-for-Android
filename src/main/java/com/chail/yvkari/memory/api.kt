package com.chail.yvkari.memory

import com.chail.yvkari.Config
import com.chail.yvkari.chat.data.Recorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

suspend fun addMemory(){
    val body = AddBody(
        user_id = Config.userId,
        messages = Recorder.getMem(),
    )
    val res = withContext(Dispatchers.IO){
        memoryApi.AddMemory(body)
    }
    Config.memCount += res.memory_nodes.size
}

suspend fun queryMemory(): List<String>{
    val body = SearchBody(
        user_id = Config.userId,
        messages = Recorder.getList()
    )
    val res = withContext(Dispatchers.IO){
        memoryApi.SearchMemory(req = body)
    }
    val resStr = mutableListOf<String>()
    if(res.memory_nodes.isNotEmpty()){
        for(it in res.memory_nodes) {
            val instant = Instant.ofEpochSecond(it.updated_at)
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            resStr.add("[$localDateTime]${it.content};")
        }
    }
    return resStr
}

package com.chail.yvkari.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chail.yvkari.Config
import com.chail.yvkari.chat.api.ChatResponse
import com.chail.yvkari.chat.api.MessageContent
import com.chail.yvkari.chat.api.getReply
import com.chail.yvkari.chat.data.AIMsg
import com.chail.yvkari.chat.data.Message
import com.chail.yvkari.chat.data.MessageRepository
import com.chail.yvkari.chat.data.MsgContent
import com.chail.yvkari.chat.data.Record
import com.chail.yvkari.chat.data.Recorder
import com.chail.yvkari.chat.data.Role
import com.chail.yvkari.chat.data.UserMsg
import com.chail.yvkari.getFullException
import com.chail.yvkari.getFullTime
import com.chail.yvkari.getTime
import com.chail.yvkari.parse
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


class ChatViewModel : ViewModel(){
    private lateinit var repo: MessageRepository
    private var timer = 0

    private var _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading.asStateFlow()

    private val messageBuff = mutableListOf<UserMsg>()
    public fun getRepo(): MessageRepository{
        return repo
    }
    public fun initRepo(ctx: Context){
        repo = MessageRepository(ctx)
    }

    public fun getMsgFlow(): Flow<List<Message>>{
        return repo.observeAllMessages()
    }
    init {
        viewModelScope.launch {
            while (true){
                delay(1000.milliseconds)
                timer++
                if(timer>= Config.delay){
                    if(!messageBuff.isEmpty()){
                        val gson = Gson()
                        while (!messageBuff.isEmpty()){
                            val msg = messageBuff[0]
                            messageBuff.removeAt(0)
                            Recorder.push(Record(role = "user", content = gson.toJson(msg)))
                        }
                        aiReply()
                    }
                    timer = 0
                }
            }
        }
    }


    public fun sendMessage(text: String){
        val userMsg = UserMsg(
            time = getFullTime(),
            content = MsgContent("text", text)
        )
        viewModelScope.launch {
            messageBuff.add(userMsg)
            repo.insertMessage(Message(
                role = Role.User,
                content = text,
                time = getTime(userMsg.time),
                timeStamp = System.currentTimeMillis()
            ))
        }
        timer = 0
    }
    private suspend fun aiReply(){
        _loading.value = true
        try {
            val res: ChatResponse = getReply()
            val replyStr = res.choices[0].message
            val reply = parse(replyStr.content, MessageContent::class.java)
                ?: throw Exception("无法解析返回内容")
            val aiMsg = AIMsg(
                time = getFullTime(),
                content = reply.contents,
                think = reply.think,
                tokens = res.usage.total_tokens
            )
            Config.tokens += aiMsg.tokens
            Recorder.push(replyStr)
            for (it in aiMsg.content) {
                delay(2000.milliseconds)
                repo.insertMessage(
                    Message(
                        role = Role.Ai,
                        content = it.content,
                        time = getTime(aiMsg.time),
                        timeStamp = System.currentTimeMillis()
                    )
                )
                Config.msgCount++
            }
        } catch (e: Exception) {
            println("网络请求错误${e.message}")
            if(Config.debugMode) repo.insertMessage(
                Message(
                    role = Role.Ai,
                    content = getFullException(e),
                    time = getTime(getFullTime()),
                    timeStamp = System.currentTimeMillis()
                )
            )
        }
        finally {
            _loading.value = false
        }
    }

}
package com.chail.yvkari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chail.yvkari.ui.theme.YvkariTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.chail.yvkari.chat.AIMsg
import com.chail.yvkari.chat.ChatResponse
import com.chail.yvkari.chat.MessageContent
import com.chail.yvkari.chat.MsgContent
import com.chail.yvkari.chat.MsgType
import com.chail.yvkari.chat.Record
import com.chail.yvkari.chat.Recorder
import com.chail.yvkari.chat.Role
import com.chail.yvkari.chat.UserMsg
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.chail.yvkari.chat.getReply
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Config.init(this)//全局配置
        ConfigDev.init(this)
        setContent {
            YvkariTheme {
                ChatPage()
            }
        }
    }
}
val DEV = true//是否处于开发模式
data class Message(//用于UI的数据
    val role: Role,
    val content: String,
)

@Composable
fun ChatPage(){
    var inputText by remember { mutableStateOf("") } //by可以封装变量的set和get
    val messageList = remember { mutableStateListOf<Message>() } //remember让变量写入内存不易丢失
    var loading by remember { mutableStateOf(false) } //mutableStateOf把数据封装为状态，检测变化并更新界面
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {//fillMaxSize()让组件填满父容器
        LazyColumn(//按需渲染
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            reverseLayout = false
        ) {
            items(messageList){msg->ChatBubble(msg)}//items展开消息列表并创建列表项
            if(loading){
                item {
                    Text(text = "输入中...", modifier = Modifier.padding(12.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = {inputText=it},//输入回调， it是输入的内容
                modifier = Modifier.weight(1f),
                placeholder = {Text("输入消息")}
            )
            Button(onClick = {
                if(inputText.isNotBlank()){
                    val userMsg = UserMsg(
                        role = Role.User,
                        content = MsgContent(MsgType.Text, inputText),
                        time = getTime()
                    )
                    inputText=""
                    messageList.add(Message(Role.User, userMsg.content.content))
                    Recorder.push(Record("user", userMsg.build()))
                    coroutineScope.launch{

                        try {
                            val res: ChatResponse = getReply()
                            val replyStr = res.choices[0].message
                            val reply = parse(replyStr.content, MessageContent::class.java)
                                ?: throw Exception("无法解析返回内容")
                            val aiMsg = AIMsg(
                                role = Role.Ai,
                                time = getTime(),
                                think = reply.think,
                                tokens = res.usage.total_tokens,
                                content = reply.contents
                            )
                            Recorder.push(Record("assistant", aiMsg.build()))
                            loading=true
                            for (it in aiMsg.content){
                                delay(2000.milliseconds)
                                messageList.add(Message(Role.Ai, it.content))
                            }
                        }
                        catch (e: Exception){
                            println("网络请求错误${e.message}")
                        }
                        finally {
                            loading=false
                        }
                    }
                }
            }) {
                Text(text = "发送")
            }
        }
    }
}

@Composable
fun ChatBubble(msg: Message){
    val isUser = msg.role== Role.User
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = if(isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(
                    if(isUser) Color(0xFF4285F4) else Color(0xFFE5E5E5),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ){
            Text(text = msg.content, color = if(isUser) Color.White else Color.Black)
        }
    }
}

fun getTime(): String{
    val sdf = SimpleDateFormat("yyyy‑MM‑dd HH:mm:ss", Locale.CHINA)
    val ms = System.currentTimeMillis()
    return sdf.format(Date(ms))
}

fun <T> parse(jsonStr:String, clazz: Class<T>):T?{
    val gson = Gson()
    return try {
        gson.fromJson(jsonStr, clazz)
    }catch (e:Exception){
        println("解析失败：$e 原始文本=$jsonStr")
        null
    }
}

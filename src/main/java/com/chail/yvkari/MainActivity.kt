package com.chail.yvkari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chail.yvkari.chat.AIMsg
import com.chail.yvkari.chat.ChatResponse
import com.chail.yvkari.chat.MessageContent
import com.chail.yvkari.chat.MsgContent
import com.chail.yvkari.chat.MsgType
import com.chail.yvkari.chat.Record
import com.chail.yvkari.chat.Recorder
import com.chail.yvkari.chat.Role
import com.chail.yvkari.chat.UserMsg
import com.chail.yvkari.chat.getReply
import com.chail.yvkari.ui.theme.YukariAvatarBg
import com.chail.yvkari.ui.theme.YukariBackground
import com.chail.yvkari.ui.theme.YukariPrimary
import com.chail.yvkari.ui.theme.YukariTextPrimary
import com.chail.yvkari.ui.theme.YukariTextSecondary
import com.chail.yvkari.ui.theme.YukariTextTertiary
import com.chail.yvkari.ui.theme.YukariUserBubble
import com.chail.yvkari.ui.theme.YvkariTheme
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
const val DEV = true//是否处于开发模式
data class Message(//用于UI的数据
    val role: Role,
    val content: String,
    val time: String
)

@Composable
fun ChatPage() {
    var inputText by remember { mutableStateOf("") }
    val messageList = remember { mutableStateListOf<Message>() }
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messageList.size) {
        if (messageList.isNotEmpty()) {
            delay(100.milliseconds)
            listState.animateScrollToItem(messageList.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(YukariBackground)) {
        // ── Top Bar ──
        YukariTopBar()

        // ── Content Area ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messageList.isEmpty() && !loading) {
                // Welcome screen
                WelcomeScreen()
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
            ) {
                items(messageList) { msg ->
                    val msgAlpha by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(300),
                        label = "msgAlpha"
                    )
                    Box(
                        modifier = Modifier.graphicsLayer(
                            alpha = msgAlpha
                        )
                    ) {
                        ChatBubble(msg)
                    }
                }

                // Animated typing indicator inside the list
                if (loading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            AvatarCircle(isUser = false, size = 40.dp)
                            Spacer(Modifier.width(8.dp))
                            TypingBubble()
                        }
                    }
                }
            }
        }

        // ── Input Bar ──
        InputBar(
            inputText = inputText,
            onValueChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    val userMsg = UserMsg(
                        role = Role.User,
                        content = MsgContent(MsgType.Text, inputText),
                        time = getFullTime()
                    )
                    val textToSend = inputText
                    inputText = ""
                    messageList.add(Message(Role.User, textToSend, getTime(userMsg.time)))
                    Recorder.push(Record("user", userMsg.build()))
                    coroutineScope.launch {
                        loading = true
                        try {
                            val res: ChatResponse = getReply()
                            val replyStr = res.choices[0].message
                            val reply = parse(replyStr.content, MessageContent::class.java)
                                ?: throw Exception("无法解析返回内容")
                            val aiMsg = AIMsg(
                                role = Role.Ai,
                                time = getFullTime(),
                                think = reply.think,
                                tokens = res.usage.total_tokens,
                                content = reply.contents
                            )
                            Recorder.push(Record("assistant", aiMsg.build()))
                            for (it in aiMsg.content) {
                                delay(2000.milliseconds)
                                messageList.add(Message(Role.Ai, it.content, getTime(aiMsg.time)))
                            }
                        } catch (e: Exception) {
                            println("网络请求错误${e.message}")
                        } finally {
                            loading = false
                        }
                    }
                }
            },
            enabled = !loading
        )
    }
}


//  Top App Bar
@Composable
fun YukariTopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = YukariPrimary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(top = 8.dp), // extra top padding for status bar inset
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar in top bar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Y",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Yvkari",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF81C784))
                    )
//                    Spacer(Modifier.width(4.dp))
//                    Text(
//                        text = "在线",
//                        color = Color.White.copy(alpha = 0.8f),
//                        fontSize = 12.sp
//                    )
                }
            }
        }
    }
}


//  Welcome Screen
@Composable
fun WelcomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Large avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(YukariAvatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Y",
                    color = YukariPrimary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "YVkari",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = YukariTextPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "开始对话",
                fontSize = 16.sp,
                color = YukariTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}


//  Avatar Circle
@Composable
fun AvatarCircle(isUser: Boolean, size: Dp) {
    val bgColor = if (isUser) Color(0xFFE8E8E8) else YukariAvatarBg
    val text = if (isUser) "U" else "Y"
    val textColor = if (isUser) YukariTextSecondary else YukariPrimary

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = if (isUser) (size.value * 0.45f).sp else (size.value * 0.5f).sp
        )
    }
}

//  Chat Bubble
@Composable
fun ChatBubble(msg: Message) {
    val isUser = msg.role == Role.User

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            AvatarCircle(isUser = false, size = 40.dp)
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Bubble
            Box(
                modifier = Modifier
                    .background(
                        color = if (isUser) YukariUserBubble else Color.White,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = msg.content,
                    color = if (isUser) Color.White else YukariTextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }

        // Spacer for user avatar
        if (isUser) {
            Spacer(Modifier.width(8.dp))
            AvatarCircle(isUser = true, size = 40.dp)
        }
    }
}

//  Typing Indicator
@Composable
fun TypingBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    // Single sine wave phase — all three dots derive from it, 120° apart
    val dotPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotPhase"
    )

    fun dotScale(index: Int): Float {
        val phaseOffset = index * 2f * PI.toFloat() / 3f
        return 0.5f + 0.4f * sin(dotPhase + phaseOffset)
    }

    Box(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 16.dp
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size((8.dp * dotScale(0)))
                    .clip(CircleShape)
                    .background(YukariPrimary.copy(alpha = 0.6f))
            )
            Box(
                modifier = Modifier
                    .size((8.dp * dotScale(1)))
                    .clip(CircleShape)
                    .background(YukariPrimary.copy(alpha = 0.6f))
            )
            Box(
                modifier = Modifier
                    .size((8.dp * dotScale(2)))
                    .clip(CircleShape)
                    .background(YukariPrimary.copy(alpha = 0.6f))
            )
        }
    }
}

//  Input Bar
@Composable
fun InputBar(
    inputText: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "输入消息...",
                        color = YukariTextTertiary
                    )
                },
                textStyle = TextStyle(
                    color = YukariTextPrimary,
                    fontSize = 15.sp
                ),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE8E8E8),
                    focusedBorderColor = YukariPrimary,
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFFFF8FA),
                    cursorColor = YukariPrimary
                ),
                maxLines = 4,
                enabled = enabled
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = onSend,
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = YukariPrimary,
                    disabledContainerColor = YukariTextTertiary
                ),
                enabled = enabled && inputText.isNotBlank()
            ) {
                Text(
                    text = "➤",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

//  Utilities
fun getTime(time: String): String {
//    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    val date = format.parse(time)
//    date?.let {
//        val calendar = Calendar.getInstance()
//        calendar.time = it
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//        return "$hour:$minute"
//    }
//    return "
    return time
}
fun getFullTime(): String {
    val sdf = SimpleDateFormat("yyyy‑MM‑dd HH:mm:ss", Locale.CHINA)
    val ms = System.currentTimeMillis()
    return sdf.format(Date(ms))
}

fun <T> parse(jsonStr: String, clazz: Class<T>): T? {
    val gson = Gson()
    return try {
        gson.fromJson(jsonStr, clazz)
    } catch (e: Exception) {
        println("解析失败：$e 原始文本=$jsonStr")
        null
    }
}
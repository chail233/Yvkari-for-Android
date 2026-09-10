package com.chail.yvkari.ui.pages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.chail.yvkari.getFullTime
import com.chail.yvkari.getTime
import com.chail.yvkari.parse
import com.chail.yvkari.ui.components.AvatarCircle
import com.chail.yvkari.ui.components.ChatBubble
import com.chail.yvkari.ui.components.InputBar
import com.chail.yvkari.ui.components.TypingBubble
import com.chail.yvkari.ui.components.WelcomeScreen
import com.chail.yvkari.ui.theme.YukariBackground
import com.chail.yvkari.ui.components.YukariTopBar
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ChatPage() {
    val ctx = LocalContext.current.applicationContext
    val repo = remember { MessageRepository(ctx) }
    val msgFlow =repo.observeAllMessages()
    var inputText by remember { mutableStateOf("") }
    val messageList by msgFlow.collectAsState(emptyList())
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
                if (inputText.trim().isNotBlank()) {
                    val userMsg = UserMsg(
                        time = getFullTime(),
                        content = MsgContent("text", inputText)
                    )
                    val textToSend = inputText
                    val gson = Gson()
                    inputText = ""
                    coroutineScope.launch {
                        Recorder.push(Record("user", gson.toJson(userMsg)))
                        repo.insertMessage(Message(
                            role = Role.User,
                            content = textToSend,
                            time = getTime(userMsg.time),
                            timeStamp = System.currentTimeMillis()
                        ))
                        loading = true
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
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chail.yvkari.ui.components.AvatarCircle
import com.chail.yvkari.ui.components.ChatBubble
import com.chail.yvkari.ui.components.DashboardSheet
import com.chail.yvkari.ui.components.InputBar
import com.chail.yvkari.ui.components.LogSheet
import com.chail.yvkari.ui.components.SettingsSheet
import com.chail.yvkari.ui.components.TypingBubble
import com.chail.yvkari.ui.components.WelcomeScreen
import com.chail.yvkari.ui.theme.YukariBackground
import com.chail.yvkari.ui.components.SnackbarManager
import com.chail.yvkari.ui.components.YukariTopBar
import com.chail.yvkari.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ChatPage() {
    val ctx = LocalContext.current.applicationContext
    val chatViewModel: ChatViewModel = viewModel()
    chatViewModel.initRepo(ctx)
    val msgFlow = chatViewModel.getMsgFlow()
    var inputText by remember { mutableStateOf("") }
    val messageList by msgFlow.collectAsState(emptyList())
    var showSettings by remember { mutableStateOf(false) }
    var showDashboard by remember { mutableStateOf(false) }
    var showLog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val loading by chatViewModel.loading.collectAsStateWithLifecycle()

    SnackbarManager.hostState = snackbarHostState


    LaunchedEffect(messageList.size) {
        if (messageList.isNotEmpty()) {
            delay(100.milliseconds)
            listState.animateScrollToItem(messageList.size - 1)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = YukariBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).background(YukariBackground)) {
            // ── Top Bar ──
            YukariTopBar(
                onDashboardClick = { showDashboard = true },
                onSettingsClick = { showSettings = true },
                onLogClick = { showLog = true }
            )

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
                onValueChange = {
                    inputText = it
                                },
                onSend = {
                    if (inputText.trim().isNotBlank()) {
                        chatViewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = true
            )
        }

        // ── Dashboard Panel (overlay, no innerPadding)
        DashboardSheet(
            visible = showDashboard,
            onDismiss = { showDashboard = false }
        )

        // ── Settings Panel (overlay, no innerPadding)
        SettingsSheet(
            visible = showSettings,
            onDismiss = { showSettings = false },
            repository = chatViewModel.getRepo()
        )

        // ── Log Panel (overlay, no innerPadding)
        LogSheet(
            visible = showLog,
            onDismiss = { showLog = false }
        )
    }
}


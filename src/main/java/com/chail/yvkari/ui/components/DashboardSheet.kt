package com.chail.yvkari.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chail.yvkari.Config
import com.chail.yvkari.ui.theme.YukariPrimary

@Composable
fun DashboardSheet(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val totalTokens = remember(visible) { Config.tokens }
    val msgCount = remember(visible) { Config.msgCount }
    val avgTokens = remember(visible) {
        if (msgCount > 0) totalTokens / msgCount else 0L
    }

    val memCount = remember { Config.memCount }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        // 全屏遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            // 右侧面板（点击内部不触发 dismiss）
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.78f)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ── 标题 ──
                    Text(
                        text = "仪表盘",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(24.dp))

                    // ── 总 Token 消耗 ──
                    StatItem(
                        label = "总 Token 消耗",
                        value = "$totalTokens"
                    )

                    Spacer(Modifier.height(20.dp))

                    // ── AI 消息条数 ──
                    StatItem(
                        label = "Yvkari 消息条数",
                        value = "$msgCount"
                    )

                    Spacer(Modifier.height(20.dp))

                    // ── 每条平均 Token 消耗 ──
                    StatItem(
                        label = "每条平均 Token 消耗",
                        value = "$avgTokens"
                    )

                    Spacer(Modifier.height(20.dp))

                    StatItem(
                        label = "记忆条数",
                        value = "$memCount"
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = YukariPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(YukariPrimary.copy(alpha = 0.04f))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
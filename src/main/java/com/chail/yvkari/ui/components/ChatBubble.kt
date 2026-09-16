package com.chail.yvkari.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chail.yvkari.chat.data.Message
import com.chail.yvkari.chat.data.Role
import com.chail.yvkari.ui.theme.YukariAiBubble
import com.chail.yvkari.ui.theme.YukariTextPrimary
import com.chail.yvkari.ui.theme.YukariUserBubble

//  Chat Bubble
@Composable
fun ChatBubble(msg: Message) {
    val isUser = msg.role == Role.User
    val isAiThink = msg.role == Role.AiThink

    // 动画状态
    var animTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animTriggered = true }

    val animAlpha by animateFloatAsState(
        targetValue = if (animTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "alpha"
    )
    val animOffsetY by animateFloatAsState(
        targetValue = if (animTriggered) 0f else 30f,
        animationSpec = tween(durationMillis = 400),
        label = "offsetY"
    )

    // AiThink 折叠状态
    var expanded by remember { mutableStateOf(false) }

    // AiThink 箭头旋转角度：折叠0°，展开90°
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "arrowRotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .alpha(animAlpha)
            .offset(y = animOffsetY.dp),
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
                        color = if (isUser) YukariUserBubble else YukariAiBubble,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                if (isAiThink) {
                    AiThinkBubbleContent(
                        content = msg.content,
                        expanded = expanded,
                        onToggle = { expanded = !expanded },
                        arrowRotation = arrowRotation
                    )
                } else {
                    Text(
                        text = msg.content,
                        color = if (isUser) Color.White else YukariTextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Spacer for user avatar
        if (isUser) {
            Spacer(Modifier.width(8.dp))
            AvatarCircle(isUser = true, size = 40.dp)
        }
    }
}

@Composable
private fun AiThinkBubbleContent(
    content: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    arrowRotation: Float
) {
    Column {
        // 折叠/展开按钮行
        Row(
            modifier = Modifier
                .clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "思考",
                color = YukariTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.width(4.dp))

            // 箭头图标在右边，用 Box 居中确保视觉对齐且绕中心旋转
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .rotate(arrowRotation),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = YukariTextPrimary,
                    fontSize = 12.sp
                )
            }
        }

        // 展开后的内容
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = content,
                color = YukariTextPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}
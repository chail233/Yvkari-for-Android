package com.chail.yvkari.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chail.yvkari.ui.theme.YukariPrimary
import kotlin.math.PI
import kotlin.math.sin

//  Typing Indicator
@Composable
fun TypingBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    // Single sine wave phase — all three dots derive from it, 120° apart
    val dotPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotPhase"
    )

    fun dotScale(index: Int): Float {
        val phaseOffset = index * 2f * PI.toFloat() / 3f
        return 0.65f + 0.25f * sin(dotPhase + phaseOffset)
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
            repeat(3) { idx ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(dotScale(idx))
                        .clip(CircleShape)
                        .background(YukariPrimary.copy(alpha = 0.6f))
                )
            }
        }
    }
}

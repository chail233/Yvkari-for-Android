package com.chail.yvkari.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.chail.yvkari.ui.theme.YukariAvatarBg
import com.chail.yvkari.ui.theme.YukariPrimary
import com.chail.yvkari.ui.theme.YukariTextSecondary

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

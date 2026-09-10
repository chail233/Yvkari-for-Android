package com.chail.yvkari.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chail.yvkari.ui.theme.YukariAvatarBg
import com.chail.yvkari.ui.theme.YukariPrimary
import com.chail.yvkari.ui.theme.YukariTextPrimary
import com.chail.yvkari.ui.theme.YukariTextSecondary

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
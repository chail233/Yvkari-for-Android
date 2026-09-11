package com.chail.yvkari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chail.yvkari.chat.data.Recorder
import com.chail.yvkari.ui.pages.ChatPage
import com.chail.yvkari.ui.theme.YvkariTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Config.init(this)//全局配置
        Recorder.init(this)
        Recorder.load()
        setContent {
            YvkariTheme {
                ChatPage()
            }
        }
    }
}
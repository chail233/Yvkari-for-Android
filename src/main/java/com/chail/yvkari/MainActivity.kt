package com.chail.yvkari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chail.yvkari.ui.pages.ChatPage
import com.chail.yvkari.ui.theme.YvkariTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Config.init(this)//全局配置
        ConfigDev.init(this)
        setContent {
            YvkariTheme {
                ChatPage()
            }
        }
    }
}
const val DEV = false//是否处于开发模式
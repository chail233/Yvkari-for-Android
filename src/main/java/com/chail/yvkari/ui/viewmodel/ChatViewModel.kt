package com.chail.yvkari.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.chail.yvkari.chat.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface ChatUiState{
    data object Loading: ChatUiState
    data class Success(val messages: List<Message>): ChatUiState
}

class ChatViewModel : ViewModel(){
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Success(emptyList()))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()


    fun sendMessage(){

    }
}
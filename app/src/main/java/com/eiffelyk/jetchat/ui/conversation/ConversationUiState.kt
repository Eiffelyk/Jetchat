package com.eiffelyk.jetchat.ui.conversation

import androidx.compose.runtime.mutableStateListOf
import com.eiffelyk.jetchat.R

class ConversationUiState(
    val channelName: String,
    val channelMembers: Int,
    initialMessages: List<Message>
) {
    private val _message: MutableList<Message> = mutableStateListOf(*initialMessages.toTypedArray())
    val messages: List<Message> = _message

    fun addMessage(msg: Message) {
        _message.add(0, msg)
    }
}

data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Int? = null,
    val authorImage: Int = if (author == "me") R.drawable.ali else R.drawable.someone_else
)
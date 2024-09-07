package com.example.rgb4u_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> get() = _messages

    fun addMessage(message: String) {
        val updatedMessages = _messages.value?.toMutableList() ?: mutableListOf()
        updatedMessages.add(message)
        _messages.value = updatedMessages
    }
}


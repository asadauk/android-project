package com.app.softwarehousemanager.models

data class ChatMessage(
    var senderId: String = "",
    var recipientId: String = "",
    var text: String = "",
    var createdAt:String = ""
)
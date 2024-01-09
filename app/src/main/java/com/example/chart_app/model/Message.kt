package com.example.chart_app.model

import com.google.firebase.Timestamp

data class Message(
    var messageId:String? = null,
    var message:String? = null,
    var senderId:String? = null,
    var imageUrl:String? = null,
    var timestamp: String? = null
)

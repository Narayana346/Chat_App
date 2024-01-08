package com.example.chart_app.model

import java.io.Serializable

data class User(
    var uid: String? = null,
    var name: String? = null,
    var phoneNO: String? = null,
    var profileImg: String? = null,
    var time: String? = null
    ):Serializable

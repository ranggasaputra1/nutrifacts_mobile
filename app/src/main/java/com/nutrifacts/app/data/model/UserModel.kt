package com.nutrifacts.app.data.model

data class UserModel(
    var id: Int,
    var token: String,
    var isLogin: Boolean = false
)

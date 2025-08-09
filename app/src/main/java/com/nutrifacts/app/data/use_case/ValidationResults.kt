package com.nutrifacts.app.data.use_case

data class ValidationResults(
    val data: String = "",
    val success: Boolean,
    val errorMsg: String? = null
)

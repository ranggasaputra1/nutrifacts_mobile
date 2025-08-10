package com.nutrifacts.app.data.use_case

class ValidateUsername {
    fun validate(username: String): ValidationResults {
        if (username.isBlank()) {
            return ValidationResults(
                success = false,
                errorMsg = "Username tidak boleh kosong"
            )
        }
        return ValidationResults(
            data = username,
            success = true
        )
    }
}
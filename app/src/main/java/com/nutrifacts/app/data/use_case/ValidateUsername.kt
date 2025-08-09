package com.nutrifacts.app.data.use_case

class ValidateUsername {
    fun validate(username: String): ValidationResults {
        if (username.isBlank()) {
            return ValidationResults(
                success = false,
                errorMsg = "Username can't be empty"
            )
        }
        return ValidationResults(
            data = username,
            success = true
        )
    }
}
package com.nutrifacts.app.data.use_case

import android.util.Patterns

class ValidateEmail {
    fun validate(email: String): ValidationResults {
        if (email.isBlank()) {
            return ValidationResults(
                success = false,
                errorMsg = "Email can't be empty"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResults(
                success = false,
                errorMsg = "Invalid email format"
            )
        }
        return ValidationResults(
            data = email,
            success = true
        )
    }
}
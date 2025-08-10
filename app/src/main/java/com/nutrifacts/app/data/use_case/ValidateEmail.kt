package com.nutrifacts.app.data.use_case

import android.util.Patterns

class ValidateEmail {
    fun validate(email: String): ValidationResults {
        if (email.isBlank()) {
            return ValidationResults(
                success = false,
                errorMsg = "Email tidak boleh kosong"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResults(
                success = false,
                errorMsg = "Format Email tidak valid"
            )
        }
        return ValidationResults(
            data = email,
            success = true
        )
    }
}
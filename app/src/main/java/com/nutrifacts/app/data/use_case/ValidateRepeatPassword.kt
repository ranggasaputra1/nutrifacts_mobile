package com.nutrifacts.app.data.use_case

class ValidateRepeatPassword {
    fun validate(password: String, repeatPassword: String): ValidationResults {
        if (password != repeatPassword) {
            return ValidationResults(
                success = false,
                errorMsg = "Passwords don't match"
            )
        }
        return ValidationResults(
            success = true
        )
    }
}
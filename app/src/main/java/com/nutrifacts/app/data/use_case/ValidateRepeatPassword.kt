package com.nutrifacts.app.data.use_case

class ValidateRepeatPassword {
    fun validate(password: String, repeatPassword: String): ValidationResults {
        if (password != repeatPassword) {
            return ValidationResults(
                success = false,
                errorMsg = "Kata sandi tidak sama"
            )
        }
        return ValidationResults(
            success = true
        )
    }
}
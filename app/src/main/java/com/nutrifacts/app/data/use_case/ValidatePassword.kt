package com.nutrifacts.app.data.use_case

class ValidatePassword {
    fun validate(password: String): ValidationResults {
        if (password.length < 8) {
            return ValidationResults(
                success = false,
                errorMsg = "Password needs to consist of at least 8 characters"
            )
        }
        val containLettersAndDigits =
            password.any { it.isDigit() } && password.any { it.isLetter() }
        if (!containLettersAndDigits) {
            return ValidationResults(
                success = false,
                errorMsg = "Password needs to contain at least one letter and digit"
            )
        }
        return ValidationResults(
            data = password,
            success = true
        )
    }
}
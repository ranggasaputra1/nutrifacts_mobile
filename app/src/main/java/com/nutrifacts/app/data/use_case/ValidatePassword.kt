package com.nutrifacts.app.data.use_case

class ValidatePassword {
    fun validate(password: String): ValidationResults {
        if (password.length < 8) {
            return ValidationResults(
                success = false,
                errorMsg = "Kata sandi harus terdiri dari minimal 8 karakter"
            )
        }
        // Tambahkan validasi untuk panjang maksimum 20 karakter
        if (password.length > 20) {
            return ValidationResults(
                success = false,
                errorMsg = "Kata sandi tidak boleh lebih dari 20 karakter"
            )
        }
        val containLettersAndDigits =
            password.any { it.isDigit() } && password.any { it.isLetter() }
        if (!containLettersAndDigits) {
            return ValidationResults(
                success = false,
                errorMsg = "Kata sandi harus mengandung setidaknya satu huruf dan satu angka"
            )
        }
        return ValidationResults(
            data = password,
            success = true
        )
    }
}
package com.nutrifacts.app.data.use_case

class ValidateTermsConditions {
    fun validate(isAccepted: Boolean): ValidationResults {
        if (!isAccepted) {
            return ValidationResults(
                success = false,
                errorMsg = "Please read and accept our terms & conditions"
            )
        }
        return ValidationResults(
            success = true
        )
    }
}
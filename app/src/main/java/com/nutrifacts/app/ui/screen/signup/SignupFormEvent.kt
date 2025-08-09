package com.nutrifacts.app.ui.screen.signup

sealed class SignupFormEvent {
    data class EmailChanged(val email: String) : SignupFormEvent()
    data class UsernameChanged(val username: String) : SignupFormEvent()
    data class PasswordChanged(val password: String) : SignupFormEvent()
    data class RepeatPasswordChanged(val repeatPassword: String) : SignupFormEvent()
    data class AcceptTermsConditionsChanged(val isAccepted: Boolean) : SignupFormEvent()

    object Submit : SignupFormEvent()
}

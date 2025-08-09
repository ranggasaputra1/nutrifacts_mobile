package com.nutrifacts.app.ui.screen.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.repository.UserRepository
import com.nutrifacts.app.data.response.SignupResponse
import com.nutrifacts.app.data.use_case.ValidateEmail
import com.nutrifacts.app.data.use_case.ValidatePassword
import com.nutrifacts.app.data.use_case.ValidateRepeatPassword
import com.nutrifacts.app.data.use_case.ValidateTermsConditions
import com.nutrifacts.app.data.use_case.ValidateUsername
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignupViewModel(
    private val repository: UserRepository,
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validateUsername: ValidateUsername = ValidateUsername(),
    private val validatePassword: ValidatePassword = ValidatePassword(),
    private val validateRepeatPassword: ValidateRepeatPassword = ValidateRepeatPassword(),
    private val validateTermsConditions: ValidateTermsConditions = ValidateTermsConditions()
) : ViewModel() {
    var state by mutableStateOf(SignupFormState())
    var emailInput by mutableStateOf("")
        private set
    var usernameInput by mutableStateOf("")
        private set
    var passwordInput by mutableStateOf("")
        private set

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun signup(email: String, username: String, password: String): Flow<Result<SignupResponse>> {
        return repository.signup(email, username, password)
    }

    fun onEvent(event: SignupFormEvent) {
        when (event) {
            is SignupFormEvent.EmailChanged -> {
                state = state.copy(email = event.email)
            }

            is SignupFormEvent.UsernameChanged -> {
                state = state.copy(username = event.username)
            }

            is SignupFormEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }

            is SignupFormEvent.RepeatPasswordChanged -> {
                state = state.copy(repeatedPassword = event.repeatPassword)
            }

            is SignupFormEvent.AcceptTermsConditionsChanged -> {
                state = state.copy(acceptedTermsConditions = event.isAccepted)
            }

            is SignupFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val emailResult = validateEmail.validate(state.email)
        val usernameResult = validateUsername.validate(state.username)
        val passwordResult = validatePassword.validate(state.password)
        val repeatPasswordResult =
            validateRepeatPassword.validate(state.password, state.repeatedPassword)
        val termsConditionsResult = validateTermsConditions.validate(state.acceptedTermsConditions)
        emailInput = emailResult.data
        usernameInput = usernameResult.data
        passwordInput = passwordResult.data

        val hasError = listOf(
            emailResult,
            usernameResult,
            passwordResult,
            repeatPasswordResult,
            termsConditionsResult
        ).any { !it.success }

        state = state.copy(
            emailError = emailResult.errorMsg,
            usernameError = usernameResult.errorMsg,
            passwordError = passwordResult.errorMsg,
            repeatedPasswordError = repeatPasswordResult.errorMsg,
            termsConditionsError = termsConditionsResult.errorMsg
        )
        if (hasError) return
        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.success)
        }
    }

    sealed class ValidationEvent {
        object success : ValidationEvent()
    }
}
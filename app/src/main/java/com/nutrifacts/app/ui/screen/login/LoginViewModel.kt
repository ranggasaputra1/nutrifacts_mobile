package com.nutrifacts.app.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.repository.UserRepository
import com.nutrifacts.app.data.use_case.ValidateEmail
import com.nutrifacts.app.data.use_case.ValidatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository,
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validatePassword: ValidatePassword = ValidatePassword()
) : ViewModel() {
    var state by mutableStateOf(LoginFormState())
    var emailInput by mutableStateOf("")
        private set
    var passwordInput by mutableStateOf("")
        private set

    private val validationEventChannel = Channel<LoginViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun saveSession(user: UserModel) {
        viewModelScope.launch { repository.saveSession(user) }
    }

    fun login(email: String, password: String): Flow<Result<UserModel>> =
        repository.login(email, password)

    fun onEvent(event: LoginFormEvent) {
        when (event) {
            is LoginFormEvent.EmailChanged -> {
                state = state.copy(email = event.email)
                updateEmail(event.email)
            }

            is LoginFormEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
                updatePassword(event.password)
            }

            is LoginFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val emailResult = validateEmail.validate(state.email)
        val passwordResult = validatePassword.validate(state.password)
        updateEmail(emailResult.data)
        updatePassword(passwordResult.data)
        val hasError = listOf(
            emailResult,
            passwordResult,
        ).any { !it.success }

        state = state.copy(
            email = emailResult.data,
            emailError = emailResult.errorMsg,
            password = passwordResult.data,
            passwordError = passwordResult.errorMsg,
        )
        if (hasError) return
        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.success)
        }
    }

    fun updateEmail(input: String) {
        emailInput = input
    }

    fun updatePassword(input: String) {
        passwordInput = input
    }

    sealed class ValidationEvent {
        object success : ValidationEvent()
    }
}
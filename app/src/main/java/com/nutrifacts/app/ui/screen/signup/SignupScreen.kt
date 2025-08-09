package com.nutrifacts.app.ui.screen.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nutrifacts.app.R
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.components.GradientButton
import com.nutrifacts.app.ui.components.LinearLoading
import com.nutrifacts.app.ui.factory.UserViewModelFactory
import com.nutrifacts.app.ui.theme.RedApple
import com.nutrifacts.app.ui.theme.YellowApple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = viewModel(
        factory = UserViewModelFactory(Injection.provideUserRepository(LocalContext.current))
    ),
    navigateToLogin: () -> Unit
) {
    val state = viewModel.state
    val context = LocalContext.current
    var loading by remember {
        mutableStateOf(false)
    }
    var showPassword by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                is SignupViewModel.ValidationEvent.success -> {
                    viewModel.signup(
                        viewModel.emailInput,
                        viewModel.usernameInput,
                        viewModel.passwordInput
                    ).collect { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    loading = true
                                }

                                is Result.Success -> {
                                    loading = false
                                    Toast.makeText(
                                        context,
                                        "Signup Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navigateToLogin()
                                }

                                is Result.Error -> {
                                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT)
                                        .show()
                                    loading = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(all = 16.dp)
        ) {
            Text(stringResource(R.string.signup), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = modifier.height(40.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(SignupFormEvent.EmailChanged(it)) },
                label = { Text(text = stringResource(id = R.string.email)) },
                isError = state.emailError != null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Mail,
                        contentDescription = "email logo"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
            )
            if (state.emailError != null) {
                Text(
                    text = state.emailError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = modifier.align(Alignment.End),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = modifier.height(10.dp))
            } else {
                Spacer(modifier = modifier.height(24.dp))
            }
            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.onEvent(SignupFormEvent.UsernameChanged(it)) },
                isError = state.usernameError != null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AlternateEmail,
                        contentDescription = "username logo"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                label = { Text(text = stringResource(id = R.string.username)) }
            )
            if (state.usernameError != null) {
                Text(
                    text = state.usernameError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = modifier.align(Alignment.End),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = modifier.height(10.dp))
            } else {
                Spacer(modifier = modifier.height(24.dp))
            }
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(SignupFormEvent.PasswordChanged(it)) },
                isError = state.passwordError != null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "password logo"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                label = { Text(text = stringResource(id = R.string.password)) },
                trailingIcon = {
                    if (showPassword) {
                        IconButton(onClick = { showPassword = false }) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = "hide_password"
                            )
                        }
                    } else {
                        IconButton(onClick = { showPassword = true }) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = "hide_password"
                            )
                        }
                    }
                }
            )
            if (state.passwordError != null) {
                Text(
                    text = state.passwordError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = modifier.align(Alignment.End),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = modifier.height(10.dp))
            } else {
                Spacer(modifier = modifier.height(24.dp))
            }
            OutlinedTextField(
                value = state.repeatedPassword,
                onValueChange = { viewModel.onEvent(SignupFormEvent.RepeatPasswordChanged(it)) },
                isError = state.repeatedPasswordError != null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "repeat password logo"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                label = { Text(text = stringResource(id = R.string.repeat_password)) },
                trailingIcon = {
                    if (showPassword) {
                        IconButton(onClick = { showPassword = false }) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = "hide_password"
                            )
                        }
                    } else {
                        IconButton(onClick = { showPassword = true }) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = "hide_password"
                            )
                        }
                    }
                }
            )
            if (state.repeatedPasswordError != null) {
                Text(
                    text = state.repeatedPasswordError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = modifier.align(Alignment.End),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = modifier.height(10.dp))
            } else {
                Spacer(modifier = modifier.height(24.dp))
            }
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = state.acceptedTermsConditions,
                    onCheckedChange = {
                        viewModel.onEvent(SignupFormEvent.AcceptTermsConditionsChanged(it))
                    }
                )
                Spacer(modifier = modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.agree_to_terms),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = modifier.height(32.dp))
            GradientButton(
                text = stringResource(R.string.create_account),
                textColor = Color.Black,
                gradient = Brush.horizontalGradient(
                    colors = listOf(
                        RedApple,
                        YellowApple
                    ),
                    startX = 25f,
                ),
                onClick = {
                    viewModel.onEvent(SignupFormEvent.Submit)
                }
            )
            Spacer(modifier = modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.not_new_to_nutrifacts),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(text = " ")
                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(Color.Blue)) {
                            append(stringResource(R.string.login))
                        }
                    },
                    onClick = { navigateToLogin() },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (state.termsConditionsError != null) {
                Text(
                    text = state.termsConditionsError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        LinearLoading(isLoading = loading, modifier.align(Alignment.BottomCenter))
    }
}
package com.nutrifacts.app.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nutrifacts.app.R
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.components.LinearLoading
import com.nutrifacts.app.ui.components.SelectionPainter
import com.nutrifacts.app.ui.components.SelectionVector
import com.nutrifacts.app.ui.factory.UserViewModelFactory
import com.nutrifacts.app.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(
        factory = UserViewModelFactory(Injection.provideUserRepository(LocalContext.current))
    )
) {
    val context = LocalContext.current
    var loading by remember {
        mutableStateOf(false)
    }
    val userSession by viewModel.getSession().collectAsState(initial = UserModel(0, "", false))

    Box(modifier = modifier.fillMaxSize()) {
        viewModel.result.collectAsState(initial = Result.Loading).value.let { user ->
            if (userSession.id != 0 && userSession.id != null) {
                when (user) {
                    is Result.Loading -> {
                        loading = true
                        viewModel.getUserById(userSession.id)
                    }

                    is Result.Success -> {
                        loading = false
                        val userData = user.data
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = modifier.padding(16.dp)
                            ) {
                                Image(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = stringResource(
                                        R.string.profile_pic
                                    ),
                                    modifier = modifier.size(120.dp)
                                )
                                Spacer(modifier = modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = userData.username.toString(),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = userData.email.toString(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                            SelectionVector(
                                icon = Icons.Default.Person,
                                label = stringResource(id = R.string.account),
                                onClick = { navController.navigate(Screen.Account.route) }
                            )
                            SelectionPainter(
                                icon = painterResource(id = R.drawable.baseline_bookmark_24),
                                label = stringResource(id = R.string.saved_product),
                                onClick = { navController.navigate(Screen.Saved.route) }
                            )
                            SelectionVector(
                                icon = Icons.Default.Notifications,
                                label = stringResource(id = R.string.notifications),
                                onClick = { navController.navigate(Screen.Notifications.route) }
                            )
                            SelectionVector(
                                icon = Icons.Default.Settings,
                                label = stringResource(id = R.string.settings),
                                onClick = { navController.navigate(Screen.Settings.route) }
                            )
                            Divider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                            SelectionVector(
                                icon = Icons.Default.ExitToApp,
                                label = stringResource(id = R.string.logout),
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.logout()
                                    }
                                }
                            )
                        }
                    }

                    is Result.Error -> {
                        loading = false
                        Toast.makeText(context, user.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        LinearLoading(
            isLoading = loading,
            modifier = modifier.align(Alignment.BottomCenter)
        )
    }
}
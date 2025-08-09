package com.nutrifacts.app.ui.screen.account

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nutrifacts.app.R
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.components.SelectionVector
import com.nutrifacts.app.ui.factory.UserViewModelFactory
import com.nutrifacts.app.ui.screen.profile.ProfileViewModel
import com.nutrifacts.app.utils.DateConverter

@Composable
fun AccountScreen(
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = stringResource(
                                    R.string.profile_pic
                                ),
                                modifier = modifier.size(120.dp)
                            )
                            Spacer(modifier = modifier.height(8.dp))
                            Row(modifier = modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(id = R.string.menu_profile),
                                    modifier = modifier
                                )
                                Spacer(modifier = modifier.width(8.dp))
                                Column(modifier = modifier) {
                                    Text(
                                        text = stringResource(id = R.string.username),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = userData.username.toString(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
//            Icon(
//                imageVector = Icons.Default.Create,
//                contentDescription = stringResource(id = R.string.edit),
//                modifier = modifier
//            )
                            }
                            Divider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = stringResource(id = R.string.date_joined),
                                    modifier = modifier
                                )
                                Spacer(modifier = modifier.width(8.dp))
                                Column(modifier = modifier) {
                                    Text(
                                        text = stringResource(id = R.string.date_joined),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = stringResource(id = R.string.date_joined) + " " + DateConverter.convertToDate(
                                            userData.createdAt.toString(),
                                        ),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                            SelectionVector(
                                icon = Icons.Default.Delete,
                                label = stringResource(id = R.string.delete_account)
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
    }
}
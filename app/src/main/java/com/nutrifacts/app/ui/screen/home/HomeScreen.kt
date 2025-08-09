package com.nutrifacts.app.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.nutrifacts.app.R
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.pref.UserPreference
import com.nutrifacts.app.data.pref.dataStore
import com.nutrifacts.app.ui.components.BigCard
import com.nutrifacts.app.ui.components.LinearLoading
import com.nutrifacts.app.ui.factory.NewsViewModelFactory

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = NewsViewModelFactory.getInstance(LocalContext.current)
    ),
    navigateToNews: (Int) -> Unit
) {
    var loading by remember {
        mutableStateOf(false)
    }

    val userSession =
        UserPreference.getInstance(LocalContext.current.dataStore).getSession().collectAsState(
            initial = UserModel(0, "", false)
        ).value

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        viewModel.news.collectAsState(initial = Result.Loading).value.let { news ->
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp)
            ) {
                item {
                    Text(text = buildAnnotatedString {
                        append(stringResource(id = R.string.welcome_back))
                    }, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.welcome_info),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = modifier.height(8.dp))
                }
                if (userSession.id != 0 && userSession.id != null) {
                    when (news) {
                        is Result.Loading -> {
                            loading = true
                            viewModel.getAllNews()
                        }

                        is Result.Success -> {
                            loading = false
                            val newsData = news.data
                            items(items = newsData, key = { it.id }) { data ->
                                BigCard(
                                    newsId = data.id,
                                    title = data.title.toString(),
                                    source = data.source.toString(),
                                    description = data.description.toString(),
                                    photoUrl = data.photoUrl,
                                    navigateToNews = navigateToNews
                                )
                                Spacer(modifier = modifier.height(16.dp))
                            }
                        }

                        is Result.Error -> {
                            loading = false
                        }
                    }
                }
            }
        }
        LinearLoading(isLoading = loading, modifier = modifier.align(Alignment.BottomCenter))
    }
}
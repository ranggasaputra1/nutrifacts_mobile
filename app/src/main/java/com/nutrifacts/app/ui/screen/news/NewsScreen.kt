package com.nutrifacts.app.ui.screen.news

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.ui.components.LinearLoading
import com.nutrifacts.app.ui.factory.NewsViewModelFactory

@Composable
fun NewsScreen(
    newsId: Int,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = NewsViewModelFactory.getInstance(LocalContext.current)
    ),
) {
    val context = LocalContext.current
    var loading by remember {
        mutableStateOf(false)
    }
    viewModel.news.collectAsState(initial = Result.Loading).value.let { news ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (news) {
                is Result.Loading -> {
                    loading = true
                    viewModel.getNewsById(newsId)
                }

                is Result.Success -> {
                    loading = false
                    val newsData = news.data
                    Column(modifier = modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = newsData.photoUrl,
                            contentDescription = "News Illustration",
                            contentScale = ContentScale.Crop,
                            modifier = modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Text(
                            text = newsData.title.toString(),
                            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = newsData.description.toString(),
                            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = newsData.date.toString(),
                            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = newsData.source.toString(),
                            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                is Result.Error -> {
                    loading = false
                }
            }
            LinearLoading(isLoading = loading, modifier = modifier.align(Alignment.BottomCenter))
        }
    }
}
package com.nutrifacts.app.ui.screen.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.pref.UserPreference
import com.nutrifacts.app.data.pref.dataStore
import com.nutrifacts.app.ui.components.SmallCard
import com.nutrifacts.app.ui.factory.ProductViewModelFactory

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ProductViewModelFactory.getInstance(LocalContext.current)
    ),
    navigateToDetail: (String) -> Unit
) {
    val user =
        UserPreference.getInstance(LocalContext.current.dataStore).getSession().collectAsState(
            initial = UserModel(0, "", false)
        ).value
    val history = viewModel.getAllHistory(user.id).collectAsState(initial = emptyList()).value
    Box(modifier = modifier.fillMaxSize()) {
        if (history.isEmpty()) {
            Text(
                text = "You don't have any history",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        } else {
            val scope = rememberCoroutineScope()
            val listState = rememberLazyListState()
            val context = LocalContext.current
            LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 80.dp)) {
                items(history, key = { it.id!! }) { data ->
                    SmallCard(
                        barcode = data.barcode,
                        name = data.name,
                        company = data.company,
                        photoUrl = data.photoUrl,
                        navigateToDetail = navigateToDetail
                    )
                }
            }
        }
    }
}
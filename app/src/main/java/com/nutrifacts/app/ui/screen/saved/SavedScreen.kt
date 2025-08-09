package com.nutrifacts.app.ui.screen.saved

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.pref.UserPreference
import com.nutrifacts.app.data.pref.dataStore
import com.nutrifacts.app.ui.components.LinearLoading
import com.nutrifacts.app.ui.components.SmallCard
import com.nutrifacts.app.ui.factory.ProductViewModelFactory

@Composable
fun SavedScreen(
    modifier: Modifier = Modifier,
    viewModel: SavedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ProductViewModelFactory.getInstance(LocalContext.current)
    ),
    navigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    var loading by remember {
        mutableStateOf(false)
    }

    val user =
        UserPreference.getInstance(LocalContext.current.dataStore).getSession().collectAsState(
            initial = UserModel(0, "", false)
        ).value

    Box(modifier = modifier.fillMaxSize()) {
        viewModel.saved.collectAsState(initial = Result.Loading).value.let { saved ->
            if (user.id != 0 && user.id != null) {
                when (saved) {
                    is Result.Loading -> {
                        loading = true
                        viewModel.getSavedProduct(user.id)
                    }

                    is Result.Success -> {
                        loading = false
                        val savedProduct = saved.data
                        if (savedProduct.isEmpty()) {
                            Text(
                                text = "You don't have any saved products",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            val scope = rememberCoroutineScope()
                            val listState = rememberLazyListState()
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(items = savedProduct, key = { it.id }) { data ->
                                    SmallCard(
                                        barcode = data.barcode.toString(),
                                        name = data.name.toString(),
                                        company = data.company.toString(),
                                        photoUrl = data.photoUrl,
                                        navigateToDetail = navigateToDetail
                                    )
                                }
                            }
                        }
                    }

                    is Result.Error -> {
                        loading = false
                        Text(
                            text = "You haven't saved any products",
                            textAlign = TextAlign.Center,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .align(Alignment.Center)
                        )
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
package com.nutrifacts.app.ui.screen.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nutrifacts.app.R
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.components.LinearLoading
import com.nutrifacts.app.ui.components.SmallCard
import com.nutrifacts.app.ui.factory.ProductViewModelFactory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ProductViewModelFactory(Injection.provideProductRepository(LocalContext.current))
    ),
    navigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    var loading by remember {
        mutableStateOf(false)
    }
    val query by viewModel.query
    viewModel.result.collectAsState(initial = Result.Loading).value.let { product ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            val scope = rememberCoroutineScope()
            val listState = rememberLazyListState()
            LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 80.dp)) {
                stickyHeader {
                    SearchBar(
                        query = query,
                        onQueryChange = viewModel::searchProducts,
                    )
                }
                when (product) {
                    is Result.Loading -> {
                        loading = true
                        viewModel.searchProducts(query)
                    }

                    is Result.Success -> {
                        loading = false
                        val productData = product.data
                        items(items = productData, key = { it.id!! }) { data ->
                            SmallCard(
                                barcode = data.barcode.toString(),
                                name = data.name.toString(),
                                company = data.company.toString(),
                                photoUrl = data.photoUrl.toString(),
                                navigateToDetail = navigateToDetail
                            )
                        }
                    }

                    is Result.Error -> {
                        loading = false
                        item {
                            Text(
                                text = "Product not found",
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
            LinearLoading(isLoading = loading, modifier = modifier.align(Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(text = stringResource(id = R.string.menu_search)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = stringResource(
                    id = R.string.menu_search
                )
            )
        },
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(min = 48.dp)
    )
}
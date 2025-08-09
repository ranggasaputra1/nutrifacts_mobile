package com.nutrifacts.app.ui.screen.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.repository.ProductRepository
import com.nutrifacts.app.data.response.ProductItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _query = mutableStateOf("")
    val query: State<String> get() = _query

    private val _result: MutableStateFlow<Result<List<ProductItem>>> =
        MutableStateFlow(Result.Loading)
    val result: StateFlow<Result<List<ProductItem>>> get() = _result

    fun searchProducts(newQuery: String) {
        _query.value = newQuery
        viewModelScope.launch {
            repository.getProductByName(query.value)
                .catch {
                    _result.value = Result.Error(it.message.toString())
                }
                .collect { result ->
                    _result.value = result
                }
        }
    }
}
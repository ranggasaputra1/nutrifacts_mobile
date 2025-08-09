package com.nutrifacts.app.ui.screen.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.repository.ProductRepository
import com.nutrifacts.app.data.response.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ScannerViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _result: MutableStateFlow<Result<Product>> =
        MutableStateFlow(Result.Loading)
    val result: StateFlow<Result<Product>> get() = _result

    fun getProductByBarcode(barcode: String) {
        viewModelScope.launch {
            repository.getProductByBarcode(barcode)
                .catch {
                    _result.value = Result.Error(it.message.toString())
                }
                .collect { result ->
                    _result.value = result
                }
        }
    }
}
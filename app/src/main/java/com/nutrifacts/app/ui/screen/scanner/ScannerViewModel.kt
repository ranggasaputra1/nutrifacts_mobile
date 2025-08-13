package com.nutrifacts.app.ui.screen.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.ProductModel
import com.nutrifacts.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ScannerViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _result: MutableStateFlow<Result<ProductModel>> =
        MutableStateFlow(Result.Loading)
    val result: StateFlow<Result<ProductModel>> get() = _result

    fun getProductByBarcode(barcode: String) {
        // Atur ulang status ke Loading di awal permintaan
        _result.value = Result.Loading
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

    // Fungsi untuk mereset status
    fun resetResult() {
        _result.value = Result.Loading
    }
}
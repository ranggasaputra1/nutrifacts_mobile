package com.nutrifacts.app.ui.screen.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.repository.ProductRepository
import com.nutrifacts.app.data.response.UserSavedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SavedViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _saved: MutableStateFlow<Result<List<UserSavedItem>>> =
        MutableStateFlow(Result.Loading)
    val saved: StateFlow<Result<List<UserSavedItem>>> get() = _saved

    fun getSavedProduct(user_id: Int) {
        viewModelScope.launch {
            repository.getSavedProduct(user_id)
                .catch {
                    _saved.value = Result.Error(it.message.toString())
                }
                .collect { result ->
                    _saved.value = result
                }
        }
    }
}
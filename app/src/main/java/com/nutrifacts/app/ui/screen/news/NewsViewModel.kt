package com.nutrifacts.app.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.repository.NewsRepository
import com.nutrifacts.app.data.response.News
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _news: MutableStateFlow<Result<News>> =
        MutableStateFlow(Result.Loading)
    val news: StateFlow<Result<News>> get() = _news

    fun getNewsById(id: Int) {
        viewModelScope.launch {
            repository.getNewsById(id)
                .catch {
                    _news.value = Result.Error(it.message.toString())
                }
                .collect { news ->
                    _news.value = news
                }
        }
    }
}
package com.nutrifacts.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.repository.NewsRepository
import com.nutrifacts.app.data.response.NewsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    private val _news: MutableStateFlow<Result<List<NewsItem>>> =
        MutableStateFlow(Result.Loading)
    val news: StateFlow<Result<List<NewsItem>>> get() = _news

    fun getAllNews() {
        viewModelScope.launch {
            newsRepository.getAllNews()
                .catch {
                    _news.value = Result.Error(it.message.toString())
                }
                .collect { news ->
                    _news.value = news
                }
        }
    }
}
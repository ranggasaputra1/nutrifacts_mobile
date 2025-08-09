package com.nutrifacts.app.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nutrifacts.app.data.repository.NewsRepository
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.screen.home.HomeViewModel
import com.nutrifacts.app.ui.screen.news.NewsViewModel

class NewsViewModelFactory(private val newsRepository: NewsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(newsRepository) as T
            }

            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(newsRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: NewsViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: NewsViewModelFactory(Injection.provideNewsRepository(context))
        }.also { instance = it }
    }
}
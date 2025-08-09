package com.nutrifacts.app.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nutrifacts.app.data.repository.ProductRepository
import com.nutrifacts.app.di.Injection
import com.nutrifacts.app.ui.screen.detail.DetailViewModel
import com.nutrifacts.app.ui.screen.history.HistoryViewModel
import com.nutrifacts.app.ui.screen.saved.SavedViewModel
import com.nutrifacts.app.ui.screen.scanner.ScannerViewModel
import com.nutrifacts.app.ui.screen.search.SearchViewModel

class ProductViewModelFactory(private val repository: ProductRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ScannerViewModel::class.java) -> {
                ScannerViewModel(repository) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SavedViewModel::class.java) -> {
                SavedViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ProductViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: ProductViewModelFactory(Injection.provideProductRepository(context))
        }.also { instance = it }
    }
}
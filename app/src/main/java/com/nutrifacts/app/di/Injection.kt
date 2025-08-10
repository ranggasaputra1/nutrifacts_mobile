package com.nutrifacts.app.di

import android.content.Context
import com.nutrifacts.app.data.local.room.HistoryDatabase
import com.nutrifacts.app.data.pref.UserPreference
import com.nutrifacts.app.data.pref.dataStore
import com.nutrifacts.app.data.repository.ProductRepository
import com.nutrifacts.app.data.repository.UserRepository
import com.nutrifacts.app.data.retrofit.APIConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = APIConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideProductRepository(context: Context): ProductRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val historyDatabase = HistoryDatabase.getDatabase(context)
        val apiService = APIConfig.getApiService(user.token)
        return ProductRepository.getInstance(historyDatabase, apiService)
    }
}
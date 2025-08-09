package com.nutrifacts.app

import androidx.lifecycle.ViewModel
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): Flow<UserModel> {
        return repository.getSession()
    }
}
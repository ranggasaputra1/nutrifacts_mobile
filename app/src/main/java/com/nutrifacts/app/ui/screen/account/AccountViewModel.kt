package com.nutrifacts.app.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.repository.UserRepository
import com.nutrifacts.app.data.response.GetUserByIdResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: UserRepository):ViewModel() {

    private val _result: MutableStateFlow<Result<GetUserByIdResponse>> =
        MutableStateFlow(Result.Loading)
    val result: StateFlow<Result<GetUserByIdResponse>> get() = _result

    fun getSession(): Flow<UserModel> {
        return repository.getSession()
    }

    fun getUserById(id: Int) {
        viewModelScope.launch {
            repository.getUserById(id)
                .catch {
                    _result.value = Result.Error(it.message.toString())
                }
                .collect { result ->
                    _result.value = result
                }
        }
    }
}
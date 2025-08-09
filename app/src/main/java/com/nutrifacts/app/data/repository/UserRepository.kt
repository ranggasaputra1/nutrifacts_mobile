package com.nutrifacts.app.data.repository

import com.google.gson.Gson
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.model.UserModel
import com.nutrifacts.app.data.pref.UserPreference
import com.nutrifacts.app.data.response.ErrorResponse
import com.nutrifacts.app.data.response.GetUserByIdResponse
import com.nutrifacts.app.data.retrofit.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: APIService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun signup(
        email: String,
        username: String,
        password: String
    ) = flow {
        emit(Result.Loading)
        try {
            val response = apiService.signup(email, username, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val response = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(response.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun login(email: String, password: String): Flow<Result<UserModel>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            val userModel = UserModel(
                response.userId!!,
                response.token!!
            )
            emit(Result.Success(userModel))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val response = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(response.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getUserById(id: Int): Flow<Result<GetUserByIdResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUserById(id)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val response = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(response.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: APIService
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(userPreference, apiService)
        }.also { instance = it }
    }

}
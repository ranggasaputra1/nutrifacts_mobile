package com.nutrifacts.app.data.repository

import com.google.gson.Gson
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.response.ErrorResponse
import com.nutrifacts.app.data.response.News
import com.nutrifacts.app.data.response.NewsItem
import com.nutrifacts.app.data.retrofit.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class NewsRepository private constructor(private val apiService: APIService) {

    fun getAllNews(): Flow<Result<List<NewsItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllNews()
            emit(Result.Success(response.news))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun getNewsById(id: Int): Flow<Result<News>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getNewsById(id)
            emit(Result.Success(response.news))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: APIService
        ) =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService)
            }.also { instance = it }
    }

}
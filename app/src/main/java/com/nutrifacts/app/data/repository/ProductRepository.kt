package com.nutrifacts.app.data.repository

import android.util.Log
import com.google.gson.Gson
import com.nutrifacts.app.data.Result
import com.nutrifacts.app.data.local.entity.History
import com.nutrifacts.app.data.local.room.HistoryDatabase
import com.nutrifacts.app.data.response.ErrorResponse
import com.nutrifacts.app.data.response.GetAllProductResponseItem
import com.nutrifacts.app.data.response.Product
import com.nutrifacts.app.data.response.ProductItem
import com.nutrifacts.app.data.response.UserSavedItem
import com.nutrifacts.app.data.retrofit.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ProductRepository private constructor(
    private val historyDatabase: HistoryDatabase,
    private val apiService: APIService
) {

    fun getAllProducts(): Flow<Result<List<GetAllProductResponseItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllProducts()
            emit(Result.Success(response.product))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun getProductByName(name: String): Flow<Result<List<ProductItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getProductByName(name)
            Log.d("repository", "$response")
            emit(Result.Success(response.product))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            Log.d("repository", "${errorResponse.message}")
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun getProductByBarcode(barcode: String): Flow<Result<Product>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getProductByBarcode(barcode)
            emit(Result.Success(response.product))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun getAllHistory(user_id: Int): Flow<List<History>> {
        return historyDatabase.historyDao().getAllHistory(user_id)
    }

    suspend fun insertHistory(history: History) {
        withContext(Dispatchers.IO) {
            historyDatabase.historyDao().insert(history)
        }
    }

    suspend fun deleteHistory(history: History) {
        withContext(Dispatchers.IO) {
            historyDatabase.historyDao().delete(history)
        }
    }

    fun getSavedProduct(user_id: Int): Flow<Result<List<UserSavedItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getSavedProduct(user_id)
            emit(Result.Success(response.userSaved))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun saveProduct(
        name: String,
        company: String,
        photoUrl: String,
        barcode: String,
        user_id: Int
    ) = flow {
        emit(Result.Loading)
        try {
            Log.d("ProductRepository", "saveProduct called with barcode: $barcode")
            val response = apiService.saveProduct(name, company, photoUrl, barcode, user_id)
            Log.d("ProductRepository", "saveProduct response: $response")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            Log.e("ProductRepository", "saveProduct error: ${e.message}", e)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteSavedProduct(id: Int) = flow {
        try {
            Log.d("ProductRepository", "deleteProduct called with id: $id")
            val response = apiService.deleteSavedProduct(id)
            Log.d("ProductRepository", "deleteProduct response: $response")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            Log.e("ProductRepository", "deleteProduct error: ${e.message}", e)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        @Volatile
        private var instance: ProductRepository? = null
        fun getInstance(
            historyDatabase: HistoryDatabase,
            apiService: APIService
        ) =
            instance ?: synchronized(this) {
                instance ?: ProductRepository(historyDatabase, apiService)
            }.also { instance = it }
    }
}
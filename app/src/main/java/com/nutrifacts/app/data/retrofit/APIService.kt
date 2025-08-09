package com.nutrifacts.app.data.retrofit

import com.nutrifacts.app.data.response.DeleteSavedProductResponse
import com.nutrifacts.app.data.response.GetAllNewsResponse
import com.nutrifacts.app.data.response.GetAllProductResponse
import com.nutrifacts.app.data.response.GetAllUserResponse
import com.nutrifacts.app.data.response.GetNewsByIdResponse
import com.nutrifacts.app.data.response.GetProductByBarcodeResponse
import com.nutrifacts.app.data.response.GetProductByNameResponse
import com.nutrifacts.app.data.response.GetSavedProductResponse
import com.nutrifacts.app.data.response.GetUserByIdResponse
import com.nutrifacts.app.data.response.LoginResponse
import com.nutrifacts.app.data.response.SaveProductResponse
import com.nutrifacts.app.data.response.SignupResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
    @FormUrlEncoded
    @POST("user/signup")
    suspend fun signup(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): SignupResponse

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("user")
    suspend fun getAllUser(
    ): GetAllUserResponse

    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): GetUserByIdResponse

    @GET("product")
    suspend fun getAllProducts(
    ): GetAllProductResponse

    @GET("product/name/{name}")
    suspend fun getProductByName(
        @Path("name") name: String
    ): GetProductByNameResponse

    @GET("product/barcode/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): GetProductByBarcodeResponse

    @GET("user/saved/{user_id}")
    suspend fun getSavedProduct(
        @Path("user_id") user_id: Int
    ): GetSavedProductResponse

    @FormUrlEncoded
    @POST("user/saved")
    suspend fun saveProduct(
        @Field("name") name: String,
        @Field("company") company: String,
        @Field("photoUrl") photoUrl: String,
        @Field("barcode") barcode: String,
        @Field("user_id") user_id: Int,
    ): SaveProductResponse

    @DELETE("user/saved/{id}")
    suspend fun deleteSavedProduct(
        @Path("id") id: Int
    ): DeleteSavedProductResponse

    @GET("news")
    suspend fun getAllNews(): GetAllNewsResponse

    @GET("news/{id}")
    suspend fun getNewsById(
        @Path("id") id: Int
    ): GetNewsByIdResponse
}
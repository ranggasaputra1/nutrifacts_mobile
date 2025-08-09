package com.nutrifacts.app.data.response

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class GetAllUserResponse(

	@field:SerializedName("data")
	val data: List<DataItem> = emptyList(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: User? = null
)

data class DataItem(

	@field:SerializedName("password")
	val password: String? = null,

	@PrimaryKey
	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("isPremium")
	val isPremium: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class User(

	@field:SerializedName("role")
	val role: Int? = null,

	@field:SerializedName("exp")
	val exp: Int? = null,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("iat")
	val iat: Int? = null,

	@field:SerializedName("username")
	val username: String? = null
)

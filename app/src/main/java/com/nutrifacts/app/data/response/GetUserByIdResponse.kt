package com.nutrifacts.app.data.response

import com.google.gson.annotations.SerializedName

data class GetUserByIdResponse(

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("isPremium")
	val isPremium: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null
)

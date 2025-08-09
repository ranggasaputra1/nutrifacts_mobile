package com.nutrifacts.app.data.response

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class GetSavedProductResponse(

	@field:SerializedName("UserSaved")
	val userSaved: List<UserSavedItem> = emptyList(),

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UserSavedItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("company")
	val company: String? = null,

	@PrimaryKey
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("barcode")
	val barcode: String? = null
)

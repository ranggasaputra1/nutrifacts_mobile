package com.nutrifacts.app.data.response

import com.google.gson.annotations.SerializedName

data class SaveProductResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

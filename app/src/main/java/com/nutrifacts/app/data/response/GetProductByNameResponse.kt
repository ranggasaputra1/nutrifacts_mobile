package com.nutrifacts.app.data.response

import com.google.gson.annotations.SerializedName
import com.nutrifacts.app.data.model.ProductModel

data class GetProductByNameResponse(
	@field:SerializedName("products")
	val products: List<ProductModel>,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
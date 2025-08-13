package com.nutrifacts.app.data.response

import com.google.gson.annotations.SerializedName
import com.nutrifacts.app.data.model.ProductModel

data class GetProductByBarcodeResponse(
	@field:SerializedName("product")
	val product: ProductModel,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
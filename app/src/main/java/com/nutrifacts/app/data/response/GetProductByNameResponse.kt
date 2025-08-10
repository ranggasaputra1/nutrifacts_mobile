package com.nutrifacts.app.data.response

import com.google.gson.annotations.SerializedName

data class GetProductByNameResponse(

	@field:SerializedName("products") // Perbaiki di sini: Ganti "product" menjadi "products"
	val products: List<ProductItem> = emptyList(),

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ProductItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("company")
	val company: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("barcode")
	val barcode: String? = null,
)
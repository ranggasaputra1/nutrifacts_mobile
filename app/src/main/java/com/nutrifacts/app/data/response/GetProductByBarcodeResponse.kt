package com.nutrifacts.app.data.response

import com.google.gson.annotations.SerializedName

data class GetProductByBarcodeResponse(

	@field:SerializedName("product")
	val product: Product = Product(),

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Product(

	@field:SerializedName("total_fat")
	val totalFat: String? = null,

	@field:SerializedName("calcium")
	val calcium: String? = null,

	@field:SerializedName("vitamin_a")
	val vitaminA: String? = null,

	@field:SerializedName("vitamin_c")
	val vitaminC: String? = null,

	@field:SerializedName("total_carbohydrate")
	val totalCarbohydrate: String? = null,

	@field:SerializedName("vitamin_d")
	val vitaminD: String? = null,

	@field:SerializedName("calories")
	val calories: String? = null,

	@field:SerializedName("saturated_fat")
	val saturatedFat: String? = null,

	@field:SerializedName("nutrition_level")
	val nutritionLevel: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("sodium")
	val sodium: String? = null,

	@field:SerializedName("protein")
	val protein: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("trans_fat")
	val transFat: String? = null,

	@field:SerializedName("cholesterol")
	val cholesterol: String? = null,

	@field:SerializedName("iron")
	val iron: String? = null,

	@field:SerializedName("company")
	val company: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("sugar")
	val sugar: String? = null,

	@field:SerializedName("barcode")
	val barcode: String? = null,

	@field:SerializedName("dietary_fiber")
	val dietaryFiber: String? = null
)

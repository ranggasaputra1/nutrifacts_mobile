package com.nutrifacts.app.data.response

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class GetAllNewsResponse(

	@field:SerializedName("news")
	val news: List<NewsItem> = emptyList(),

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class NewsItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@PrimaryKey
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)

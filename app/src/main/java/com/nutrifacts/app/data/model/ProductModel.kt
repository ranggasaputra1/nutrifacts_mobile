package com.nutrifacts.app.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    @field:SerializedName("id")
    var id: Int,

    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("company")
    var company: String,

    @field:SerializedName("photoUrl")
    var photoUrl: String,

    @field:SerializedName("calories")
    var calories: String,

    @field:SerializedName("fat")
    var fat: String,

    @field:SerializedName("saturated_fat")
    var saturatedFat: String,

    @field:SerializedName("trans_fat")
    var transFat: String,

    @field:SerializedName("cholesterol")
    var cholesterol: String,

    @field:SerializedName("sodium")
    var sodium: String,

    @field:SerializedName("carbohydrate")
    var carbohydrate: String,

    @field:SerializedName("dietary_fiber")
    var dietaryFiber: String,

    @field:SerializedName("sugar")
    var sugar: String,

    @field:SerializedName("proteins")
    var proteins: String,

    @field:SerializedName("calcium")
    var calcium: String,

    @field:SerializedName("iron")
    var iron: String,

    @field:SerializedName("vitamin_a")
    var vitaminA: String,

    @field:SerializedName("vitamin_c")
    var vitaminC: String,

    @field:SerializedName("vitamin_d")
    var vitaminD: String,

    @field:SerializedName("nutrition_level")
    var nutritionLevel: String,

    @field:SerializedName("barcode")
    var barcode: String,

    @field:SerializedName("information")
    var information: String,

    @field:SerializedName("keterangan")
    var keterangan: String
) : Parcelable
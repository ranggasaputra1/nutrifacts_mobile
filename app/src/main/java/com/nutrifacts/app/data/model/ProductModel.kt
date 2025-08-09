package com.nutrifacts.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    var photoUrl: String = "",
    var nutritionData: String = "",
    var name: String,
    var company: String,
    var id: Int,
    var barcode: String,
    var nutritionLevel: String = ""
) : Parcelable

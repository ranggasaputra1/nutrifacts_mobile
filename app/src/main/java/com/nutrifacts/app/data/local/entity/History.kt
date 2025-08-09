package com.nutrifacts.app.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class History(
    @field:PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    var name: String,
    var company: String,
    var photoUrl: String,
    var barcode: String,
    var user_id:Int,
    val dateAdded: String
) : Parcelable

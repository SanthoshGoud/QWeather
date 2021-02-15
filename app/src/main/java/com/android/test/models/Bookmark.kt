package com.android.test.models

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Bookmark (
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var placeId: String? = null,
    var name: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var phone: String = "",
    var  notes: String = ""
):Serializable {
//
//    //3
//    companion object {
//        fun generateImageFilename(id: Long): String {
//// 4
//            return "bookmark$id.png"
//        }
//    }
}
package com.example.doublem.data.hid

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hids")
data class Hid (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageUri : String
)
package com.example.locationbitacora

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 1,

    @ColumnInfo(name = "lat")
    val lat: String?,

    @ColumnInfo(name = "log")
    val log: String?
)
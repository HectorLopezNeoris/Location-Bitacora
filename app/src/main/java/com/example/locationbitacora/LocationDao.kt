package com.example.locationbitacora

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg location: Location)

    @Delete
    fun delete(location: Location)

    @Query("SELECT * FROM locations")
    fun getAll() : List<Location>
}


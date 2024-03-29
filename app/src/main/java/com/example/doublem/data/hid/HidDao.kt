package com.example.doublem.data.hid

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HidDao {
    @Insert
    suspend fun insert(hid: Hid) : Long
    @Update
    suspend fun update(hid: Hid)

    @Delete
    suspend fun delete(hid: Hid)

    @Query("SELECT * from hids WHERE id = :id")
    fun getHid(id: Int): Flow<Hid>

    @Query("SELECT * from hids ORDER BY name ASC")
    fun getAllHids(): Flow<List<Hid>>

}
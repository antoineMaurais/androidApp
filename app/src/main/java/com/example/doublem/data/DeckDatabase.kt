package com.example.doublem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.doublem.data.hid.Hid
import com.example.doublem.data.hid.HidDao

@Database(entities = [Hid::class], version = 1, exportSchema = false)
abstract class DeckDatabase : RoomDatabase() {
    abstract fun hidDao(): HidDao

    companion object {
        @Volatile
        private var Instance: DeckDatabase? = null

        fun getDatabase(context: Context): DeckDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, DeckDatabase::class.java, "hid_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}
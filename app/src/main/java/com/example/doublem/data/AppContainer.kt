package com.example.doublem.data

import android.content.Context
import com.example.doublem.data.hid.HidsRepository

interface AppContainer {
    val hidsRepository : HidsRepository
}


class AppDataContainer(private val context: Context) : AppContainer {

    override val hidsRepository: HidsRepository by lazy{
        OfflineHidsRepository(DeckDatabase.getDatabase(context).hidDao())
    }
}



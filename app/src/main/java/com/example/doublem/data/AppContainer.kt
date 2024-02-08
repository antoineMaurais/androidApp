package com.example.doublem.data

import android.content.Context

interface AppContainer {
    val hidsRepository : HidsRepository
}


class AppDataContainer(private val context: Context) : AppContainer {

    override val hidsRepository: HidsRepository by lazy{
        OfflineHidsRepository(DeckDatabase.getDatabase(context).hidDao())
    }
}



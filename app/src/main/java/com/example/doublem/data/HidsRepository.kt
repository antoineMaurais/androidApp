package com.example.doublem.data

import kotlinx.coroutines.flow.Flow

interface HidsRepository {
    /**
     * Retrieve all the hids from the the given data source.
     */
    fun getAllHidsStream(): Flow<List<Hid>>

    /**
     * Retrieve an hid from the given data source that matches with the [id].
     */
    fun getHidStream(id: Int): Flow<Hid?>

    /**
     * Insert hid in the data source
     */
    suspend fun insertHid(hid: Hid)

    /**
     * Delete hid from the data source
     */
    suspend fun deleteHid(hid: Hid)

    /**
     * Update hid in the data source
     */
    suspend fun updateHid(hid: Hid)

}
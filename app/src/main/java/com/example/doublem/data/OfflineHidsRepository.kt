package com.example.doublem.data

import kotlinx.coroutines.flow.Flow

//class OfflineHidsRepository (private val hidDao: HidDao) : HidsRepository {
//    override fun getAllHidsStream(): Flow<List<Hid>> = hidDao.getAllHids()
//
//    override fun getHidStream(id: Int): Flow<Hid?> = hidDao.getHid(id)
//
//    override suspend fun insertHid(hid: Hid) = hidDao.insert(hid)
//
//    override suspend fun deleteHid(hid: Hid) = hidDao.delete(hid)
//
//    override suspend fun updateHid(hid: Hid) = hidDao.update(hid)
//}

class OfflineHidsRepository(private val hidDao: HidDao) : HidsRepository {

    override fun getAllHidsStream(): Flow<List<Hid>> {
        return hidDao.getAllHids()
    }

    override fun getHidStream(id: Int): Flow<Hid?> {
        return hidDao.getHid(id)
    }

    override suspend fun insertHid(hid: Hid) {
        hidDao.insert(hid)
    }

    override suspend fun deleteHid(hid: Hid) {
        hidDao.delete(hid)
    }

    override suspend fun updateHid(hid: Hid) {
        hidDao.update(hid)
    }
}
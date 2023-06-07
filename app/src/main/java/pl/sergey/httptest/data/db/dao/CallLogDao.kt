package pl.sergey.httptest.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pl.sergey.httptest.data.db.model.CallLogEntity

@Dao
interface CallLogDao {

    @Query("SELECT * FROM call_logs")
    fun loadAll(): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs LIMIT 1")
    fun loadFirst(): Flow<CallLogEntity?>

    @Query("SELECT * FROM call_logs")
    suspend fun getAll(): List<CallLogEntity>

    @Query("SELECT * FROM call_logs WHERE remoteId IN (:ids)")
    suspend fun getInRemoteIds(ids: List<Long>): List<CallLogEntity>

    @Upsert
    suspend fun upsert(items: List<CallLogEntity>)

}
package pl.sergey.httptest.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.sergey.httptest.data.db.model.CallLogEntity

interface CallLogRepository {

    fun loadLogs(): Flow<List<CallLogEntity>>
    fun getLog() : List<CallLogEntity>

}
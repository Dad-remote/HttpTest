package pl.sergey.httptest.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import pl.sergey.httptest.data.db.HttpTestDatabase
import pl.sergey.httptest.data.db.model.CallLogEntity
import pl.sergey.httptest.domain.repository.CallLogRepository
import pl.sergey.httptest.data.support.CallLogFetcher
import java.util.LinkedList

class CallLogRepositoryImpl(
    private val callLogFetcher: CallLogFetcher,
    private val database: HttpTestDatabase
) : CallLogRepository {

    override fun loadLogs(): Flow<List<CallLogEntity>> {
        return database.callLogDao().loadAll()
    }

    override fun getLog() : List<CallLogEntity> {
        return runBlocking {
            val logs = callLogFetcher.getCallLog()
            saveNewItems(logs)

            val allItems = database.callLogDao().getAll()
            database.callLogDao().upsert(allItems.map { it.copy(timesQuered = it.timesQuered + 1) })
            return@runBlocking allItems
        }
    }

    private suspend fun saveNewItems(logs: List<CallLogEntity>) {
        val ids = logs.map { it.remoteId }
        val localItems = database.callLogDao().getInRemoteIds(ids)
        val map = HashMap<Long, CallLogEntity>()
        localItems.forEach { map[it.remoteId] = it }
        val result = LinkedList<CallLogEntity>()
        logs.forEach {
            map[it.remoteId]?.let { localItem ->
                result.add(it.copy(id = localItem.id, timesQuered = localItem.timesQuered))
            } ?: run {
                result.add(it)
            }
        }
        database.callLogDao().upsert(result)
    }

}
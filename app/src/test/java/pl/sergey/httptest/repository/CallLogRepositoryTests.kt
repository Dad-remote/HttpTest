package pl.sergey.httptest.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import pl.sergey.httptest.data.db.HttpTestDatabase
import pl.sergey.httptest.data.db.dao.CallLogDao
import pl.sergey.httptest.data.db.model.CallLogEntity
import pl.sergey.httptest.data.repository.CallLogRepositoryImpl
import pl.sergey.httptest.data.support.CallLogFetcher

class CallLogRepositoryTests {

    private val emptyCallLog: List<CallLogEntity> = emptyList()
    private val cachedCallLog = listOf(
        CallLogEntity(1, 2, 1, 1, "+48123123123", "test", 0),
        CallLogEntity(2, 3, 2, 2, "+48123123123", "test", 0),
        CallLogEntity(3, 4, 3, 3, "+48123123123", "test", 0)
    )
    private val fetcherCallLog = listOf(
        CallLogEntity(0, 2, 1, 1, "+48123123123", "test", 0),
        CallLogEntity(0, 3, 2, 2, "+48123123123", "test", 0),
        CallLogEntity(0, 4, 3, 3, "+48123123123", "test", 0),
        CallLogEntity(0, 5, 4, 4, "+48123123123", "test", 0),
    )
    private val mergedCallLog = listOf(
        CallLogEntity(1, 2, 1, 1, "+48123123123", "test", 0),
        CallLogEntity(2, 3, 2, 2, "+48123123123", "test", 0),
        CallLogEntity(3, 4, 3, 3, "+48123123123", "test", 0),
        CallLogEntity(4, 5, 4, 4, "+48123123123", "test", 0),
    )

    private val callLogFetcher: CallLogFetcher = mock()
    private val emptyCallLogFetcher: CallLogFetcher = mock {
        on { getCallLog() } doReturn emptyCallLog
    }
    private val fullCallLogFetcher: CallLogFetcher = mock {
        on { getCallLog() } doReturn fetcherCallLog
    }

    private var storage = ArrayList<CallLogEntity>()

    private fun save() : (InvocationOnMock) -> Unit {
        return { invocation ->
            val items = invocation.arguments.first() as List<CallLogEntity>
            val ids = items.map { it.id }
            val idsSet = HashSet(ids)
            var maxId = (storage.maxByOrNull { it.id }?.id ?: 0) + 1
            storage.removeAll(storage.filter { idsSet.contains(it.id) })

            storage.addAll(items.map { if (it.id == 0L) it.copy(id = maxId++) else it })
        }
    }

    private fun filtered(items: List<CallLogEntity>) : (InvocationOnMock) -> List<CallLogEntity> {
        return { invocation ->
            items.filter { HashSet<Long>(invocation.arguments.first() as List<Long>).contains(it.remoteId) }
        }
    }

    private fun buildDatabase(items: List<CallLogEntity>): HttpTestDatabase {
        storage.addAll(items)
        val callLogDao: CallLogDao = mock {
            onBlocking { getAll() } doReturn storage
            onBlocking { loadAll() } doReturn flow { emit(storage) }
            onBlocking { getInRemoteIds(ArgumentMatchers.anyList()) } doAnswer filtered(storage)
            onBlocking { upsert(ArgumentMatchers.anyList()) } doAnswer save()
        }
        return mock {
            on { callLogDao() } doReturn callLogDao
        }
    }

    @Before
    fun setup() {
        storage.clear()
    }

    @Test
    fun `get call logs from empty cache EXPECT return empty list`() = runBlocking {
        val database: HttpTestDatabase = buildDatabase(emptyCallLog)
        val callLogRepository = CallLogRepositoryImpl(callLogFetcher, database)

        val actualItems = callLogRepository.loadLogs()

        Assert.assertEquals(emptyCallLog, actualItems.first())
    }

    @Test
    fun `get cached call logs EXPECT return values from db`() = runBlocking {
        val database: HttpTestDatabase = buildDatabase(cachedCallLog)
        val callLogRepository = CallLogRepositoryImpl(callLogFetcher, database)

        val actualItems = callLogRepository.loadLogs()

        Assert.assertEquals(cachedCallLog, actualItems.first())
    }

    @Test
    fun `get call logs with empty db and empty fetcher EXPECT return empty list`() = runBlocking {
        val database = buildDatabase(emptyCallLog)
        val callLogRepository = CallLogRepositoryImpl(emptyCallLogFetcher, database)

        val actualItems = callLogRepository.getLog()

        Assert.assertEquals(emptyCallLog, actualItems)
    }

    @Test
    fun `get call logs with cached items and empty fetcher EXPECT return cached items`() = runBlocking {
        val database = buildDatabase(cachedCallLog)
        val callLogRepository = CallLogRepositoryImpl(emptyCallLogFetcher, database)

        val actualItems = callLogRepository.getLog()

        Assert.assertEquals(cachedCallLog.map { it.copy(timesQuered = 1) }, actualItems)
    }

    @Test
    fun `get call logs with empty db and full fetcher EXPECT return items from fetcher`() = runBlocking {
        val database = buildDatabase(emptyCallLog)
        val callLogRepository = CallLogRepositoryImpl(fullCallLogFetcher, database)

        val actualItems = callLogRepository.getLog()

        Assert.assertEquals(fetcherCallLog.map { it.copy(id = 0, timesQuered = 1) }, actualItems.map { it.copy(id = 0) })
    }

    @Test
    fun `get call logs with cached items and full fetcher EXPECT return items from fetcher`() = runBlocking {
        val database = buildDatabase(cachedCallLog)
        val callLogRepository = CallLogRepositoryImpl(fullCallLogFetcher, database)

        val actualItems = callLogRepository.getLog()

        Assert.assertEquals(mergedCallLog.map { it.copy(timesQuered = 1) }, actualItems)
    }

    @Test
    fun `get call logs twice EXPECT return items with incremented timesQuered field`() = runBlocking {
        val database = buildDatabase(cachedCallLog)
        val callLogRepository = CallLogRepositoryImpl(emptyCallLogFetcher, database)

        val firstItems = callLogRepository.getLog()
        Assert.assertEquals(cachedCallLog.map { it.copy(timesQuered = it.timesQuered + 1) }, firstItems)

        val secondItems = callLogRepository.getLog()
        Assert.assertEquals(cachedCallLog.map { it.copy(timesQuered = it.timesQuered + 2) }, secondItems)
    }
}
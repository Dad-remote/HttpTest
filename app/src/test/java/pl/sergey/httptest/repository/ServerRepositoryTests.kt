package pl.sergey.httptest.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import pl.sergey.httptest.data.db.HttpTestDatabase
import pl.sergey.httptest.data.repository.ServerRepositoryImpl
import pl.sergey.httptest.data.server.model.ServerState
import pl.sergey.httptest.data.support.CallLogFetcher

class ServerRepositoryTests {

    private val callLogFetcher: CallLogFetcher = mock()
    private val database: HttpTestDatabase = mock()
    private val defaultState = ServerState("", 0, false)

    @Test
    fun `check default value of service EXPECT service is not active`() = runBlocking {
        val callLogRepository = ServerRepositoryImpl(callLogFetcher, database)

        val state = callLogRepository.serverState

        Assert.assertEquals(false, state.first().active)
    }

    @Test
    fun `turn on and off the service EXPECT service is changing the state`() = runBlocking {
        val callLogRepository = ServerRepositoryImpl(callLogFetcher, database)

        callLogRepository.toggleServer()
        val firstState = callLogRepository.serverState.first()
        callLogRepository.toggleServer()
        val secondState = callLogRepository.serverState.first()

        Assert.assertEquals(true, firstState.active)
        Assert.assertEquals(false, secondState.active)
    }

    @Test
    fun `check start time of service EXPECT start time is set when service is started`() = runBlocking {
        val callLogRepository = ServerRepositoryImpl(callLogFetcher, database)
        val currentTime = System.currentTimeMillis()
        val argumentCaptor = ArgumentCaptor.forClass(Long::class.java)

        callLogRepository.toggleServer()
        val state = callLogRepository.serverState.first()

        Assert.assertEquals(true, state.active)
        verify(callLogFetcher).setStartTime(argumentCaptor.capture())
        Assert.assertTrue(currentTime <= argumentCaptor.value)
    }
}
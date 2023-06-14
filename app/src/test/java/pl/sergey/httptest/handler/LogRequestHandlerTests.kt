package pl.sergey.httptest.handler

import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import pl.sergey.httptest.data.db.model.CallLogEntity
import pl.sergey.httptest.data.server.handler.LogRequestHandler
import pl.sergey.httptest.data.server.model.CallLogResponse
import pl.sergey.httptest.domain.repository.CallLogRepository
import pl.sergey.httptest.util.capture
import pl.sergey.net.Metadata

class LogRequestHandlerTests {


    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<List<CallLogResponse>>

    @Test
    fun `check output values EXPECT correct values`() {
        val logs = listOf(
            CallLogEntity(
                id = 1,
                remoteId = 1,
                startTime = System.currentTimeMillis(),
                duration = 1,
                number = "+48123123123",
                name = "TestName",
                timesQuered = 1
            ),
            CallLogEntity(
                id = 2,
                remoteId = 2,
                startTime = System.currentTimeMillis(),
                duration = 2,
                number = "+48123123123",
                name = "TestName",
                timesQuered = 2
            )
        )
        val expectedResult = logs.map { CallLogResponse(it) }
        val callLogRepository: CallLogRepository = mock {
            on { getLog() } doReturn logs
        }
        val metadata: Metadata = mock()
        argumentCaptor = ArgumentCaptor.forClass(expectedResult::class.java)
        val handler = LogRequestHandler(callLogRepository)

        handler.handle(metadata)

        verify(metadata).json(capture(argumentCaptor))

        Assert.assertEquals(expectedResult.size, argumentCaptor.value.size)
        argumentCaptor.value.forEach { responseItem ->
            Assert.assertNotNull(expectedResult.firstOrNull {
                it.beginning == responseItem.beginning &&
                it.duration == responseItem.duration &&
                it.number == responseItem.number &&
                it.name == responseItem.name &&
                it.timesQueried == responseItem.timesQueried
            })
        }
    }
}
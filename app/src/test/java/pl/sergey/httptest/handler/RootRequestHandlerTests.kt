package pl.sergey.httptest.handler

import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import pl.sergey.httptest.data.server.handler.RootRequestHandler
import pl.sergey.httptest.data.server.model.RootInfo
import pl.sergey.httptest.data.server.model.ServiceInfo
import pl.sergey.httptest.data.support.IPHolder
import pl.sergey.httptest.data.support.formatTime
import pl.sergey.httptest.util.capture
import pl.sergey.net.Metadata

class RootRequestHandlerTests {

    @Captor private lateinit var argumentCaptor: ArgumentCaptor<RootInfo>

    @Test
    fun `check output values EXPECT correct values`() {
        val startTime = System.currentTimeMillis()
        val ip = "192.168.1.2"
        val port = 10000
        val expectedResult = buildExpectedResult(startTime, ip, port)
        val ipHolder: IPHolder = mock {
            on { getIp() } doReturn ip
        }
        val metadata: Metadata = mock()
        val handler = RootRequestHandler(startTime, ipHolder, port)
        argumentCaptor = ArgumentCaptor.forClass(RootInfo::class.java)

        handler.handle(metadata)

        verify(metadata).json(capture(argumentCaptor))
        Assert.assertEquals(expectedResult.start, argumentCaptor.value.start)
        Assert.assertEquals(expectedResult.services.size, argumentCaptor.value.services.size)
        argumentCaptor.value.services.forEach { currentService ->
            Assert.assertNotNull(expectedResult.services.firstOrNull { it == currentService })
        }
    }

    private fun buildExpectedResult(startTime: Long, ip: String, port: Int): RootInfo {
        val logServiceName = "log"
        val logServiceUrl = "http://${ip}:${port}/${logServiceName}"
        val statusServiceName = "status"
        val statusServiceUrl = "http://${ip}:${port}/${statusServiceName}"
        return RootInfo(
            start = formatTime(startTime),
            services = listOf(
                ServiceInfo(logServiceName, logServiceUrl),
                ServiceInfo(statusServiceName, statusServiceUrl)
            )
        )
    }
}
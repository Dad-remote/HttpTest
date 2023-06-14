package pl.sergey.httptest.handler

import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import pl.sergey.httptest.data.server.handler.StatusRequestHandler
import pl.sergey.httptest.data.server.model.StatusResponse
import pl.sergey.httptest.data.support.ActiveCallChecker
import pl.sergey.httptest.data.support.CallDataHolder
import pl.sergey.net.Metadata

class StatusRequestHandlerTests {

    @Test
    fun `check output values EXPECT correct values`() {
        val expectedResult = StatusResponse(ongoing = false, number = "+48123123123", name = "TestName")
        val callDataHolder: CallDataHolder = mock {
            on { last } doReturn (expectedResult.number to expectedResult.name)
        }
        val callChecker: ActiveCallChecker = mock {
            on { isCallActive() } doReturn expectedResult.ongoing
        }
        val metadata: Metadata = mock()
        val handler = StatusRequestHandler(callChecker, callDataHolder)

        handler.handle(metadata)

        verify(metadata).json(expectedResult)
    }

}
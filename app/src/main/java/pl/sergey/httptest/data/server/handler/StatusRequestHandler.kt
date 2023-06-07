package pl.sergey.httptest.data.server.handler

import pl.sergey.httptest.data.server.model.StatusResponse
import pl.sergey.httptest.data.support.ActiveCallChecker
import pl.sergey.httptest.data.support.CallDataHolder
import pl.sergey.net.Metadata
import pl.sergey.net.RequestHandler
import pl.sergey.net.Response
import javax.inject.Inject

class StatusRequestHandler @Inject constructor(
    private val callChecker: ActiveCallChecker,
    private val callDataHolder: CallDataHolder
) : RequestHandler {

    override fun handle(response: Metadata): Response {
        val (number, name) = callDataHolder.last
        return response.json(StatusResponse(ongoing = callChecker.isCallActive(), number = number, name = name))
    }

}
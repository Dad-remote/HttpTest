package pl.sergey.httptest.data.server.handler

import pl.sergey.httptest.domain.repository.CallLogRepository
import pl.sergey.httptest.data.server.model.CallLogResponse
import pl.sergey.net.Metadata
import pl.sergey.net.RequestHandler
import pl.sergey.net.Response
import javax.inject.Inject

class LogRequestHandler @Inject constructor(private val callLogRepository: CallLogRepository) : RequestHandler {

    override fun handle(response: Metadata): Response {
        val logs = callLogRepository.getLog()
        return response.json(logs.map { CallLogResponse(it) })
    }

}
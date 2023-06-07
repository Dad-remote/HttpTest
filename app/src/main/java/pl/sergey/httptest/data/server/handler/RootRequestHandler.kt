package pl.sergey.httptest.data.server.handler

import pl.sergey.httptest.data.support.IPHolder
import pl.sergey.httptest.data.support.formatTime
import pl.sergey.httptest.data.server.model.RootInfo
import pl.sergey.httptest.data.server.model.ServiceInfo
import pl.sergey.net.Metadata
import pl.sergey.net.RequestHandler
import pl.sergey.net.Response

class RootRequestHandler(private val startTime: Long, private val ipHolder: IPHolder, private val port: Int) :
    RequestHandler {

    companion object {
        const val LOG_SERVICE_NAME = "log"
        const val STATUS_SERVICE_NAME = "status"
    }

    override fun handle(response: Metadata): Response {
        val statusUrl = "http://${ipHolder.getIp()}:${port}/$STATUS_SERVICE_NAME"
        val logUrl = "http://${ipHolder.getIp()}:${port}/$LOG_SERVICE_NAME"

        val services = listOf(
            ServiceInfo(STATUS_SERVICE_NAME, statusUrl),
            ServiceInfo(LOG_SERVICE_NAME, logUrl)
        )
        return response.json(RootInfo(formatTime(startTime), services))
    }
}
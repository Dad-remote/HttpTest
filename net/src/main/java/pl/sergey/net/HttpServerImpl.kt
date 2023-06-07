package pl.sergey.net

import android.net.Uri
import fi.iki.elonen.NanoHTTPD

class HttpServerImpl(override val port: Int) : NanoHTTPD(port), HttpServer {

    companion object {
        val NOT_FOUND_RESPONSE = newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found")
    }

    init {
        start(SOCKET_READ_TIMEOUT, false);
    }

    private val handlersGet = HashMap<String, RequestHandler>()

    private fun pathFrom(uri: String?): String {
        return uri?.let { Uri.parse(it).path } ?: "/"
    }

    override fun serve(session: IHTTPSession?): Response {
        return super.serve(session)
    }

    override fun serve(
        uri: String?,
        method: Method?,
        headers: MutableMap<String, String>?,
        parms: MutableMap<String, String>?,
        files: MutableMap<String, String>?
    ): Response {
        return when (method) {
            Method.GET -> handlersGet[pathFrom(uri)]?.handle(Metadata())?.getHttpdResponse() ?: NOT_FOUND_RESPONSE
            else -> NOT_FOUND_RESPONSE
        }
    }

    override fun get(path: String, handler: RequestHandler) {
        handlersGet[path] = handler
    }
}
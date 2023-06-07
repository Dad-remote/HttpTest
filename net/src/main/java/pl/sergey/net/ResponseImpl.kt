package pl.sergey.net

import fi.iki.elonen.NanoHTTPD

class ResponseImpl(private val item: NanoHTTPD.Response?) : Response {

    override fun getHttpdResponse(): NanoHTTPD.Response? = item
}
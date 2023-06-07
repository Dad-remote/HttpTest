package pl.sergey.net

import fi.iki.elonen.NanoHTTPD

interface Response {

    fun getHttpdResponse() : NanoHTTPD.Response?

}
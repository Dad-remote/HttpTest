package pl.sergey.net

import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status

class Metadata {

    companion object {
        const val MIME_JSON = "application/json"
    }

    fun json(data: Any): Response {
        return ResponseImpl(NanoHTTPD.newFixedLengthResponse(Status.OK, MIME_JSON, Gson().toJson(data)))
    }
}
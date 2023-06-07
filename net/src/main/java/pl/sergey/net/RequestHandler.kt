package pl.sergey.net

interface RequestHandler {

    fun handle(response: Metadata): Response?

}
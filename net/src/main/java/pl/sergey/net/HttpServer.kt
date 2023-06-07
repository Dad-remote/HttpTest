package pl.sergey.net

interface HttpServer {

    companion object {
        fun build(port: Int) = HttpServerImpl(port)
    }

    val port: Int

    fun get(path: String, handler: RequestHandler)
    fun stop()
}
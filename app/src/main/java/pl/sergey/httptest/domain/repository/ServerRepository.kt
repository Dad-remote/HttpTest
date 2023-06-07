package pl.sergey.httptest.domain.repository

import kotlinx.coroutines.flow.MutableStateFlow
import pl.sergey.httptest.data.server.model.ServerState

interface ServerRepository {

    val serverState: MutableStateFlow<ServerState>

    fun onServerStateChanged(serverState: ServerState)
    fun toggleServer()

}
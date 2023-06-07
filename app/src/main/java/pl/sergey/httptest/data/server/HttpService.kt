package pl.sergey.httptest.data.server

import kotlinx.coroutines.flow.Flow
import pl.sergey.httptest.data.server.model.ServerState

interface HttpService {

    val serverState: Flow<ServerState>

}
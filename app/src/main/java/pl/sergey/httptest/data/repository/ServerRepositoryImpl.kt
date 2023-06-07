package pl.sergey.httptest.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.sergey.httptest.data.server.model.ServerState
import pl.sergey.httptest.data.db.HttpTestDatabase
import pl.sergey.httptest.domain.repository.ServerRepository
import pl.sergey.httptest.data.support.CallLogFetcher

class ServerRepositoryImpl(
    private val callLogFetcher: CallLogFetcher,
    private val database: HttpTestDatabase
) : ServerRepository {

    override val serverState: MutableStateFlow<ServerState> = MutableStateFlow(ServerState("<empty>", 0, false))

    override fun onServerStateChanged(serverState: ServerState) {
        this.serverState.value = serverState
    }

    override fun toggleServer() {
        val active = !serverState.value.active
        serverState.value = serverState.value.copy(active = active)
        if (active) {
            callLogFetcher.setStartTime(System.currentTimeMillis())
        }
        GlobalScope.launch(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

}
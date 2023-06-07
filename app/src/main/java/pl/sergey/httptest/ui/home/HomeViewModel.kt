package pl.sergey.httptest.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import pl.sergey.httptest.data.server.model.ServerState
import pl.sergey.httptest.data.db.model.CallLogEntity
import pl.sergey.httptest.domain.repository.CallLogRepository
import pl.sergey.httptest.domain.repository.ServerRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    private val callLogRepository: CallLogRepository
)  : ViewModel() {

    val serverState: StateFlow<ServerState> = serverRepository.serverState
    val logs: Flow<List<CallLogEntity>> = callLogRepository.loadLogs()

    fun toggleServer() {
        serverRepository.toggleServer()
    }
}
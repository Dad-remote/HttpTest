package pl.sergey.httptest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.sergey.httptest.domain.repository.ServerRepository
import pl.sergey.httptest.data.server.HttpService
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private var activityJob: Job? = null

    val active = serverRepository.serverState.map { it.active }

    fun connected(httpService: HttpService) {
        activityJob = viewModelScope.launch {
            httpService.serverState.collect {
                serverRepository.onServerStateChanged(it)
            }
        }
    }

    fun disconnected() {
        activityJob?.cancel()
    }

}
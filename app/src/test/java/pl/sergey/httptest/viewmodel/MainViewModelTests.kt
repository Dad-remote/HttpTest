package pl.sergey.httptest.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import pl.sergey.httptest.data.repository.ServerRepositoryImpl
import pl.sergey.httptest.data.server.HttpService
import pl.sergey.httptest.data.server.model.ServerState
import pl.sergey.httptest.ui.MainViewModel

class MainViewModelTests {

    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `check state updates from service EXPECT viewModel spreads new states`() = runBlocking {
        val serverRepository = ServerRepositoryImpl(mock(), mock())
        val viewModel = MainViewModel(serverRepository)

        serverRepository.onServerStateChanged(ServerState("", 0, false))

        Assert.assertEquals(false, viewModel.active.first())

        serverRepository.onServerStateChanged(ServerState("", 0, true))

        Assert.assertEquals(true, viewModel.active.first())
    }

    @Test
    fun `check saving state updates EXPECT viewModel saves new states`() = runBlocking {
        val defaultState = ServerState("", 0, false)
        val nextState = ServerState("192.168.1.2", 10000, true)
        val states = MutableStateFlow(defaultState)
        val serverRepository: ServerRepositoryImpl = mock()
        val httpService: HttpService = mock {
            on { serverState } doReturn states
        }
        val viewModel = MainViewModel(serverRepository)

        viewModel.connected(httpService)

        verify(serverRepository).onServerStateChanged(defaultState)

        states.value = nextState

        verify(serverRepository).onServerStateChanged(nextState)

        viewModel.disconnected()
    }
}
package pl.sergey.httptest.viewmodel

import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import pl.sergey.httptest.domain.repository.ServerRepository
import pl.sergey.httptest.ui.home.HomeViewModel

class HomeViewModelTests {

    @Test
    fun `check the action to toggle the service EXPECT toggle fun of repository is called`() {
        val serverRepository: ServerRepository = mock()
        val viewModel = HomeViewModel(serverRepository, mock())

        viewModel.toggleServer()

        verify(serverRepository).toggleServer()
    }
}
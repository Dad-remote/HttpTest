package pl.sergey.httptest.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import pl.sergey.httptest.ui.home.Home

@Composable
fun Main(
    appState: HttpTestAppState = rememberHttpTestAppState()
) {
    NavHost(navController = appState.navController, startDestination = "home") {
        composable("home") { Home() }
    }
}

@Composable
fun rememberHttpTestAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
) : HttpTestAppState {
    return remember(navController, coroutineScope) {
        HttpTestAppState(navController, coroutineScope)
    }
}

@Stable
class HttpTestAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope
)
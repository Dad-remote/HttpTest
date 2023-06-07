package pl.sergey.httptest.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.sergey.httptest.R
import pl.sergey.httptest.data.db.model.CallLogEntity
import pl.sergey.httptest.data.support.formatTime

@Composable
fun Home(modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.serverState.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle(initialValue = emptyList())

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (button, text, list) = createRefs()

        Text(
            text = "ip: ${state.ip}, port: ${state.port}",
            modifier = Modifier.constrainAs(text) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(button.bottom, margin = 16.dp)
                start.linkTo(button.start, margin = 16.dp)
            }
        )
        Button(
            onClick = { viewModel.toggleServer() },
            modifier = Modifier.constrainAs(button) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }
        ) {
            Text(
                text = stringResource(if (state.active) R.string.stop else R.string.start),
                modifier = modifier
            )
        }
        LazyColumn(modifier = Modifier.constrainAs(list) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            top.linkTo(text.bottom, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            start.linkTo(parent.start, margin = 16.dp)
            bottom.linkTo(parent.bottom, margin = 16.dp)
        }) {
            items(count = logs.size, key = { logs[it].id }, itemContent = { index ->
                ListItem(item = logs[index])
            })
        }
    }
}

@Composable
fun ListItem(modifier: Modifier = Modifier, item: CallLogEntity) {
    Text(
        text = "name: ${item.name}, number: ${item.number}",
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)
    )
    Text(
        text = "time: ${formatTime(item.startTime)}, duration: ${item.duration}s",
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
    )
}

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import components.PreviewDialog
import theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
@Preview
fun App(
    viewModel: MainViewModel,
    window: WindowState
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ViewEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ViewEffect.ProjectGenerated -> {
                    snackbarHostState.showSnackbar("Project generated at: ${effect.filePath}")
                }
            }
        }
    }

    AppTheme(darkTheme = state.darkMode) {
        Box(modifier = Modifier.fillMaxSize()) {

            TestingMainLayoutGrid(
                window = window,
                viewModel = viewModel,
                state = state,
                coroutineScope = coroutineScope,
                modifier = Modifier.fillMaxSize(),
                lazyListState = listState
            )

            if (state.isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (state.showPreview) {
                PreviewDialog(
                    state = state,
                    onDismiss = {
                        viewModel.processIntent(ViewIntent.HidePreview)
                    }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun StyledCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.padding(5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 6.dp
        )
    ) {
        content()
    }
}
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowState
import theme.AppTheme

@Composable
@Preview
fun App(
    viewModel: MainViewModel,
    window: WindowState
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Header(window, viewModel::processIntent)
                }

                item {
                    HorizontalDivider()
                }

                item {
                    ProjectInformationSection(state, viewModel::processIntent)
                }

                item {
                    UIConfigSection(state, viewModel::processIntent)
                }

                item {
                    DependencySection(state, viewModel::processIntent)
                }

                item {
                    GenerateButton(state, viewModel::processIntent)
                }
            }

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
                PreviewDialog(state) {
                    viewModel.processIntent(ViewIntent.HidePreview)
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
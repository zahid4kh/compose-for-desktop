
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import components.PreviewDialog
import deskit.dialogs.file.filesaver.FileSaverDialog
import deskit.dialogs.info.InfoDialog
import theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
@Preview
fun App(
    viewModel: MainViewModel,
    window: WindowState
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(window.size.width) {
        isExpanded = window.size.width > 600.dp
    }

    AppTheme(darkTheme = state.darkMode) {
        Box(modifier = Modifier.fillMaxSize()) {

            TestingMainLayoutGrid(
                window = window,
                viewModel = viewModel,
                state = state,
                coroutineScope = coroutineScope,
                modifier = Modifier.fillMaxSize(),
                lazyListState = listState,
                isExpanded = isExpanded
            )

            if (state.isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Generating your project...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
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

            if (state.showFileSaver) {
                FileSaverDialog(
                    title = "Save Project As",
                    suggestedFileName = state.suggestedFileName,
                    extension = ".zip",
                    onSave = { file ->
                        viewModel.processIntent(ViewIntent.SaveProjectToFile(file))
                    },
                    onCancel = {
                        viewModel.processIntent(ViewIntent.HideFileSaver)
                    }
                )
            }

            if (state.showSuccessDialog) {
                InfoDialog(
                    title = "Project Generated Successfully!",
                    message = state.successMessage,
                    onClose = {
                        viewModel.processIntent(ViewIntent.HideSuccessDialog)
                    }
                )
            }

            if (state.showErrorDialog) {
                InfoDialog(
                    title = "Error",
                    message = state.errorMessage,
                    onClose = {
                        viewModel.processIntent(ViewIntent.HideErrorDialog)
                    }
                )
            }
        }
    }
}
// App.kt
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.launch
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

    var isExpanded by remember { mutableStateOf(false) }

    // Watch window width changes
    LaunchedEffect(window.size.width) {
        isExpanded = window.size.width > 600.dp
    }

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
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Header(window, state, viewModel::processIntent, viewModel::toggleDarkMode)
                }

                item {
                    HorizontalDivider()
                }

                item {
                    AnimatedContent(
                        targetState = isExpanded,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)).togetherWith(fadeOut(animationSpec = tween(300)))
                        }
                    ) { expanded ->
                        if (expanded) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StyledCard {
                                        ProjectInformationSection(state, viewModel::processIntent)
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StyledCard {
                                        UIConfigSection(state, viewModel::processIntent)
                                    }
                                }
                            }
                        } else {
                            Column {
                                StyledCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    ProjectInformationSection(state, viewModel::processIntent)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                StyledCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    UIConfigSection(state, viewModel::processIntent)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DependencySection(state, viewModel::processIntent)
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GenerateButton(state, viewModel::processIntent)

                        Spacer(modifier = Modifier.width(16.dp))

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text("Go to Top")
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Go to Top"
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
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
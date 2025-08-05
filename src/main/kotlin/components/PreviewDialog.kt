package components
import ProjectOptions
import ViewState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import theme.getJetbrainsMonoFamily
import tobegenerated.PreviewFunctions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewDialog(
    state: ViewState,
    onDismiss: () -> Unit
) {
    val options = ProjectOptions(
        appName = state.appName,
        packageName = state.packageName,
        projectVersion = state.projectVersion,
        windowWidth = state.windowWidth,
        windowHeight = state.windowHeight,
        includeRetrofit = state.dependencies["Retrofit"] ?: false,
        includeDeskit = state.dependencies["Deskit"] ?: true,
        includeSQLDelight = state.dependencies["SQLDelight"] ?: false,
        includeKtor = state.dependencies["Ktor"] ?: false,
        includeDecompose = state.dependencies["Decompose"] ?: false,
        includeImageLoader = state.dependencies["ImageLoader"] ?: false,
        includePrecompose = state.dependencies["Precompose"] ?: false,
        includeSentry = state.dependencies["Sentry"] ?: false,
        includeMarkdown = state.dependencies["Markdown"] ?: false,
        includeHotReload = state.dependencies["HotReload"] ?: true,
        includeKotlinxDatetime = state.dependencies["KotlinxDatetime"] ?: false,
        appDescription = state.appDescription,
        linuxMaintainer = state.linuxMaintainer
    )

    val previewVerticalScrollState = rememberScrollState(0)
    val previewHorizontalScrollState = rememberScrollState(0)

    DialogWindow(
        onCloseRequest = onDismiss,
        state = rememberDialogState(
            size = DpSize(800.dp, 800.dp),
            position = WindowPosition.Aligned(Alignment.Center)
        ),
        title = "Preview Generated Files"
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Preview Generated Files",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text("Close Dialog")
                            }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                }

                HorizontalDivider()

                var selectedTab by remember { mutableStateOf(0) }
                val tabs = listOf(
                    "build.gradle.kts",
                    "settings.gradle.kts",
                    "libs.versions.toml",
                    "Main.kt",
                    "README.md"
                )

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                        tabs = {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 12.sp,
                                            overflow = TextOverflow.Ellipsis,
                                            softWrap = false

                                        )
                                    },
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                )
                            }
                        })

                    // Code preview area
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val content = when (selectedTab) {
                            0 -> PreviewFunctions.generateBuildGradlePreview(options)
                            1 -> PreviewFunctions.generateSettingsGradlePreview(options)
                            2 -> PreviewFunctions.generateVersionCatalogPreview(options)
                            3 -> PreviewFunctions.generateMainFilePreview(options)
                            4 -> PreviewFunctions.generateReadmePreview(options)
                            else -> ""
                        }

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = content,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = getJetbrainsMonoFamily(),
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(previewVerticalScrollState)
                                        .horizontalScroll(previewHorizontalScrollState)
                                )

                                VerticalScrollbar(
                                    adapter = rememberScrollbarAdapter(previewVerticalScrollState),
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxHeight()
                                        .pointerHoverIcon(PointerIcon.Hand),
                                    style = LocalScrollbarStyle.current.copy(
                                        hoverColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unhoverColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                    )
                                )

                                HorizontalScrollbar(
                                    adapter = rememberScrollbarAdapter(previewHorizontalScrollState),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth()
                                        .pointerHoverIcon(PointerIcon.Hand),
                                    style = LocalScrollbarStyle.current.copy(
                                        hoverColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        unhoverColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package components

import projectgen.ViewIntent
import projectgen.ViewState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DependencySection(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit
) {
    val depMaps = mapOf(
        "HotReload" to "Enable live code updates without restarting.",
        "Deskit" to "Material3 FileChooser and dialogs.",
        "Decompose" to "Component-based navigation.",
        "Ktor" to "Kotlin-first HTTP client.",
        "Markdown" to "Display Markdown content.",
        "Sentry" to "Error tracking & performance.",
        "Retrofit" to "Type-safe HTTP client for API calls.",
        "ImageLoader" to "Efficient image loading and caching",
        "SQLDelight" to "Type-safe SQL for local database.",
        "Precompose" to "Navigation, ViewModel, DI.",
        "KotlinxDatetime" to "Date and time library for Kotlin."
    )
    val title by remember { mutableStateOf("Additional Dependencies") }
    val localGridState = rememberLazyGridState()

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        start = Offset(0f, 0f),
        end = Offset(600f, 600f)
    )

    OutlinedCard(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(cardGradient)
            .padding(20.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider()

            Box(modifier = Modifier.fillMaxSize()){
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(215.dp),
                    state = localGridState,
                    modifier = Modifier.height(400.dp).padding(end = 8.dp)
                ) {
                    items(
                        count = depMaps.size,
                        itemContent = { index ->
                            val entry = depMaps.entries.elementAt(index)
                            DependencyItem(
                                name = entry.key,
                                description = entry.value,
                                selected = state.dependencies[entry.key] ?: false,
                                onClick = { isSelected ->
                                    onIntent(ViewIntent.ToggleDependency(entry.key, isSelected))
                                },
                                modifier = Modifier.animateItem(placementSpec = spring())
                            )
                        }
                    )
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(localGridState),
                    modifier = Modifier
                        .height(400.dp)
                        .align(Alignment.TopEnd)
                        .padding(vertical = 8.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                    style = LocalScrollbarStyle.current.copy(
                        hoverColor = MaterialTheme.colorScheme.primary,
                        unhoverColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                )
            }

        }
    }
}

@Composable
fun DependencyItem(
    name: String,
    description: String,
    selected: Boolean,
    onClick: (Boolean) -> Unit,
    modifier: Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    var mousePosition by remember { mutableStateOf(Offset.Zero) }
    val colors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        MaterialTheme.colorScheme.surface
    )

    val hoverGradient = remember(mousePosition, isHovered) {
        if (isHovered) {
            object : ShaderBrush() {
                override fun createShader(size: Size): Shader {
                    val radius = 120f
                    return RadialGradientShader(
                        center = mousePosition,
                        radius = radius,
                        colors = colors,
                        colorStops = listOf(0f, 0.7f, 1f)
                    )
                }
            }
        } else {
            null
        }
    }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .padding(8.dp)
            .animateContentSize()
            .pointerInput(Unit) {
                detectHoverEvents(
                    onEnter = {
                        isHovered = true
                        mousePosition = it
                    },
                    onExit = { isHovered = false }
                ) { position ->
                    mousePosition = position
                }
            }
    ) {
        Column(
            modifier = Modifier.then(
                if (hoverGradient != null) {
                    Modifier.background(hoverGradient)
                } else {
                    Modifier
                }
            ).padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onClick(!selected) },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                )

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private suspend fun PointerInputScope.detectHoverEvents(
    onEnter: (Offset) -> Unit = {},
    onExit: () -> Unit = {},
    onMove: (Offset) -> Unit = {}
) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            when (event.type) {
                PointerEventType.Enter -> {
                    onEnter(event.changes.first().position)
                }
                PointerEventType.Exit -> {
                    onExit()
                }
                PointerEventType.Move -> {
                    onMove(event.changes.first().position)
                }
            }
        }
    }
}
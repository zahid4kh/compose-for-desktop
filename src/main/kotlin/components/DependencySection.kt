package components

import ViewIntent
import ViewState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
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

    OutlinedCard(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider()

            LazyVerticalGrid(
                columns = GridCells.Adaptive(215.dp),
                state = rememberLazyGridState(),
                modifier = Modifier.height(400.dp).padding(top = 16.dp)
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
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .padding(8.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
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
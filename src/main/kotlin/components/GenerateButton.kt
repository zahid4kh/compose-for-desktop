package components

import projectgen.ViewIntent
import projectgen.ViewState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

@Composable
fun GenerateButton(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit
) {
    val generateConditions = !state.isGenerating &&
            state.appName.isNotBlank() &&
            state.packageName.isNotBlank() &&
            state.packageNameError.isEmpty()

    val buttonGradient = if (generateConditions) {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.inversePrimary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary
            ),
            start = Offset(0f, 0f),
            end = Offset(200f, 50f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }

    Button(
        onClick = { onIntent(ViewIntent.GenerateProject) },
        shape = MaterialTheme.shapes.medium,
        enabled = generateConditions,
        modifier = if(generateConditions) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier
    ) {
        Text(if (state.isGenerating) "Generating..." else "Generate Project")
    }
}
package components

import ViewIntent
import ViewState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GenerateButton(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit
) {
    Button(
        onClick = { onIntent(ViewIntent.GenerateProject) },
        shape = MaterialTheme.shapes.medium,
        enabled = !state.isGenerating &&
                state.appName.isNotBlank() &&
                state.packageName.isNotBlank() &&
                state.packageNameError.isEmpty()
    ) {
        Text(if (state.isGenerating) "Generating..." else "Generate Project")
    }
}
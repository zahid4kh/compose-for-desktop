import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenerateButton(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center
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
}
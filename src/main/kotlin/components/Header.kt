package components
import ViewIntent
import ViewState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState

@Composable
fun Header(
    windowState: WindowState,
    state: ViewState,
    onIntent: (ViewIntent) -> Unit,
    onToggleDarkMode: () -> Unit
) {
    var expandClicked by remember { mutableStateOf(false) }

    if (expandClicked) {
        windowState.size = windowState.size.copy(width = 900.dp)
    } else {
        windowState.size = windowState.size.copy(width = 480.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compose for Desktop Wizard",
            fontSize = 22.sp,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Desktop Client",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    expandClicked = !expandClicked
                }
            ) {
                Icon(
                    imageVector = if (expandClicked) Icons.Default.ExpandLess else Icons.Default.Expand,
                    contentDescription = "Toggle Width"
                )
            }

            IconButton(
                onClick = { onIntent(ViewIntent.ShowPreview) }
            ) {
                Icon(
                    imageVector = Icons.Default.Preview,
                    contentDescription = "Preview"
                )
            }

            // Theme toggle button
            IconButton(
                onClick = onToggleDarkMode
            ) {
                Icon(
                    imageVector = if (state.darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }
        }
    }
}
package components
import ViewIntent
import ViewState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import composefordesktop.resources.Res
import composefordesktop.resources.maximize
import composefordesktop.resources.minimize
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
            .padding(vertical = 10.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compose for Desktop Wizard",
            style = MaterialTheme.typography.titleLargeEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = "Desktop Client",
            style = MaterialTheme.typography.titleSmallEmphasized,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    expandClicked = !expandClicked
                }
            ) {
                Icon(
                    painter = painterResource(if(expandClicked) Res.drawable.minimize else Res.drawable.maximize),
                    contentDescription = "Window size toggle",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { onIntent(ViewIntent.ShowPreview) }
            ) {
                Icon(
                    imageVector = Icons.Default.Preview,
                    contentDescription = "Preview",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Theme toggle button
            IconButton(
                onClick = onToggleDarkMode
            ) {
                Icon(
                    imageVector = if (state.darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
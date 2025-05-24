import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState
import composefordesktop.resources.Inter_VariableFont
import composefordesktop.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun Header(
    windowState: WindowState,
    onIntent: (ViewIntent) -> Unit
) {
    var expandClicked by remember { mutableStateOf(false) }

    if (expandClicked) {
        windowState.size = windowState.size.copy(width = 800.dp)
    } else {
        windowState.size = windowState.size.copy(width = 480.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compose for Desktop Wizard",
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.Bold,
            ))
        )

        Text(
            text = "Desktop Client",
            fontFamily = FontFamily(Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.Normal,
            ))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
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
        }
    }
}
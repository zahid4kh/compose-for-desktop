import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composefordesktop.resources.Inter_VariableFont
import composefordesktop.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun UIConfigSection(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            text = "UI CONFIGURATION",
            fontFamily = FontFamily(Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.Bold,
            )),
            fontSize = 20.sp
        )
        HorizontalDivider()

        UIConfigItem(
            itemTitle = "Default Window Width (dp)",
            placeholderText = "800",
            value = state.windowWidth,
            onValueChange = { onIntent(ViewIntent.UpdateWindowWidth(it)) },
        )

        UIConfigItem(
            itemTitle = "Default Window Height (dp)",
            placeholderText = "600",
            value = state.windowHeight,
            onValueChange = { onIntent(ViewIntent.UpdateWindowHeight(it)) },
        )
    }
}

@Composable
fun UIConfigItem(
    itemTitle: String = "",
    placeholderText: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {}
) {
    Column {
        Text(
            text = itemTitle,
            fontFamily = FontFamily(
                Font(
                    Res.font.Inter_VariableFont,
                    weight = FontWeight.Bold
                )
            ),
            fontSize = 16.sp
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholderText) },
            shape = MaterialTheme.shapes.medium,
        )
    }
}
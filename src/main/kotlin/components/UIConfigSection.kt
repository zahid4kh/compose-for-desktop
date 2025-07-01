package components

import ViewIntent
import ViewState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UIConfigSection(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit,
    modifier : Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(20.dp)
    ) {
        Text(
            text = "UI CONFIGURATION",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 20.sp
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)

        UIConfigItem(
            itemTitle = "Default Window Width (dp)",
            placeholderText = "800",
            value = state.windowWidth,
            onValueChange = { onIntent(ViewIntent.UpdateWindowWidth(it)) },
            modifier = Modifier.animateContentSize()
        )

        UIConfigItem(
            itemTitle = "Default Window Height (dp)",
            placeholderText = "600",
            value = state.windowHeight,
            onValueChange = { onIntent(ViewIntent.UpdateWindowHeight(it)) },
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
fun UIConfigItem(
    itemTitle: String = "",
    placeholderText: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = itemTitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholderText) },
            shape = MaterialTheme.shapes.medium,
        )
    }
}
package components

import ViewIntent
import ViewState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UIConfigSection(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit,
    modifier : Modifier
) {
    var expandUiConfig by rememberSaveable { mutableStateOf(true) }

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.surfaceVariant
        ),
        start = Offset(500f, 0f),
        end = Offset(0f, 500f)
    )

    OutlinedCard(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(cardGradient)
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "UI CONFIGURATION",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 20.sp
                )
                IconButton(
                    onClick = {expandUiConfig = !expandUiConfig},
                    modifier = Modifier
                        .size(22.dp)
                        .pointerHoverIcon(PointerIcon.Hand),
                ){
                    Icon(
                        imageVector = if(expandUiConfig) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)

            AnimatedVisibility(
                visible = expandUiConfig
            ){
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
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
//
//                    // Spacer to match height with ProjectInformationSection
//                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
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
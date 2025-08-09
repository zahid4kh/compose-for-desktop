package components

import projectgen.ViewIntent
import projectgen.ViewState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProjectInformationSection(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit,
    modifier: Modifier
) {
    var expandProjectInfo by rememberSaveable { mutableStateOf(true) }

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.surfaceVariant
        ),
        start = Offset(0f, 0f),
        end = Offset(500f, 500f)
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
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = "PROJECT INFORMATION",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = {expandProjectInfo = !expandProjectInfo},
                    modifier = Modifier
                        .size(22.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                ){
                    Icon(
                        imageVector = if(expandProjectInfo) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)

            AnimatedVisibility(
                visible = expandProjectInfo
            ){
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    ProjectInfoItem(
                        itemTitle = "Application Name*",
                        placeholderText = "My Compose App",
                        value = state.appName,
                        onValueChange = { onIntent(ViewIntent.UpdateAppName(it)) },
                        subtitle = "The name of your application. Used for window title and app name",
                        modifier = Modifier.animateContentSize()
                    )

                    ProjectInfoItem(
                        itemTitle = "Package Name*",
                        placeholderText = "myapp",
                        value = state.packageName,
                        onValueChange = { onIntent(ViewIntent.UpdatePackageName(it)) },
                        subtitle = state.packageNameError.ifEmpty {
                            "The package name of your application. E.g. codeeditor"
                        },
                        isError = state.packageNameError.isNotEmpty(),
                        modifier = Modifier.animateContentSize()
                    )

                    ProjectInfoItem(
                        itemTitle = "Version",
                        placeholderText = "1.0.0",
                        value = state.projectVersion,
                        onValueChange = { onIntent(ViewIntent.UpdateVersion(it)) },
                        subtitle = "The version of your application. Used for versioning your app",
                        modifier = Modifier.animateContentSize()
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectInfoItem(
    itemTitle: String = "",
    placeholderText: String = "",
    subtitle: String? = null,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false,
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
            singleLine = true,
            supportingText = {
                Text(
                    text = subtitle ?: "",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = isError
        )
    }
}
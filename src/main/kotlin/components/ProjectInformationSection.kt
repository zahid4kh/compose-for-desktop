package components

import ViewIntent
import ViewState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProjectInformationSection(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit,
    modifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .padding(20.dp)
    ) {
        Text(
            text = "PROJECT INFORMATION",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp
        )
        HorizontalDivider()

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
            style = MaterialTheme.typography.bodyLarge
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
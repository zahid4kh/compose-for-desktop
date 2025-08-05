package components

import ViewIntent
import ViewState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LinuxDevAuthorSection(
    modifier: Modifier,
    state: ViewState,
    onIntent: (ViewIntent) -> Unit,
){
    var expandLinuxDevConfig by rememberSaveable { mutableStateOf(true) }

    OutlinedCard(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(20.dp)
        ){
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "FOR LINUX BUILDS",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 20.sp
                )
                IconButton(
                    onClick = {expandLinuxDevConfig = !expandLinuxDevConfig},
                    modifier = Modifier
                        .size(22.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                ){
                    Icon(
                        imageVector = if(expandLinuxDevConfig) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)

            AnimatedVisibility(
                visible = expandLinuxDevConfig
            ){
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    LinuxDevConfigItem(
                        itemTitle = "Maintainer",
                        placeholderText = "Name Surname <youremail@gmail.com>",
                        value = state.linuxMaintainer,
                        onValueChange = {maintainer ->
                            onIntent(ViewIntent.SetLinuxMaintainer(maintainer))
                        },
                        modifier = Modifier.animateContentSize()
                    )

                    LinuxDevConfigItem(
                        itemTitle = "Description",
                        placeholderText = "Your project description",
                        value = state.appDescription,
                        onValueChange = {description ->
                            onIntent(ViewIntent.SetProjectDescription(description))
                        },
                        modifier = Modifier.animateContentSize()
                    )
                }
            }
        }
    }
}


@Composable
fun LinuxDevConfigItem(
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
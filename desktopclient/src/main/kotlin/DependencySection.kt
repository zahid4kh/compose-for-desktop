
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composefordesktop.resources.Inter_VariableFont
import composefordesktop.resources.Res
import org.jetbrains.compose.resources.Font

@Preview
@Composable
fun DependencySection(
){
    val depMaps = mapOf(
        "Hot Reload" to "Enable live code updates without restarting.",
        "Deskit" to "Material3 FileChooser and dialogs.",
        "Decompose" to "Component-based navigation.",
        "Ktor Client" to "Kotlin-first HTTP client.",
        "Markdown Renderer" to "Display Markdown content.",
        "Sentry" to "Error tracking & performance.",
        "Retrofit + OkHttp" to "Type-safe HTTP client for API calls.",
        "Image Loading" to "(e.g., Coil, KMP-ImageLoader)",
        "SQLDelight" to "Type-safe SQL for local database.",
        "PreCompose" to "Navigation, ViewModel, DI.",
        "kotlinx.datetime" to "Date and time library for Kotlin."
    )

    val selectedDeps = remember { mutableStateListOf<String>() }


    Column(
        modifier = Modifier.padding(16.dp)
    ){
        Text(
            text = "ADDITIONAL DEPENDENCIES",
            fontFamily = FontFamily(Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.Bold,
            )),
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        HorizontalDivider()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = rememberLazyGridState(),
        modifier = Modifier.height(500.dp)
    ){
        items(
            count = depMaps.size,
            itemContent = { index ->
                val entry = depMaps.entries.elementAt(index)
                DependencyItem(
                    name = entry.key,
                    description = entry.value,
                    selected = selectedDeps.contains(entry.key),
                    onClick = { isSelected ->
                        if(isSelected){
                            selectedDeps.add(entry.key)
                        }else{
                            selectedDeps.remove(entry.key)
                        }
                    }
                )
            }
        )
    }

}


@Composable
fun DependencyItem(
    name: String,
    description: String,
    selected: Boolean,
    onClick: (Boolean) -> Unit
){
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.padding(10.dp)
    ){
        Column(
            modifier = Modifier.padding(8.dp)
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                RadioButton(
                    selected = selected,
                    onClick  = {onClick(!selected)},
                )

                Column(
                    horizontalAlignment = Alignment.Start
                ){
                    Text(
                        text = name,
                        fontFamily = FontFamily(
                            Font(
                                resource = Res.font.Inter_VariableFont,
                                weight = FontWeight.Bold
                            )
                        )
                    )

                    Text(
                        text = description,
                        fontFamily = FontFamily(
                            Font(
                                resource = Res.font.Inter_VariableFont,
                                weight = FontWeight.Normal
                            )
                        ),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
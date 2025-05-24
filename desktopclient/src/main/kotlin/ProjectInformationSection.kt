import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import composefordesktop.resources.*
import org.jetbrains.compose.resources.Font

@Composable
fun ProjectInformationSection(){
    var version by remember { mutableStateOf("1.0.0") }
    var appName by remember { mutableStateOf("My Cool App") }
    var packageName by remember { mutableStateOf("mycoolapp") }

    LaunchedEffect(version){
        println("Version: $version")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(16.dp)
    ){
        Text(
            text = "PROJECT INFORMATION",
            fontFamily = FontFamily(Font(
                Res.font.Inter_VariableFont,
                weight = FontWeight.Bold,
            )),
            fontSize = 20.sp
        )
        HorizontalDivider()

        ProjectInfoItem(
            itemTitle = "Application Name*",
            placeholderText = appName,
            value = appName,
            onValueChange = {appName = it},
            subtitle = "The name of your application. Used for window title and app name"
        )

        ProjectInfoItem(
            itemTitle = "Package Name*",
            placeholderText = packageName,
            value = packageName,
            onValueChange = {packageName = it},
            subtitle = "The package name of your application. Can be used for launching your app from terminal"
        )

        ProjectInfoItem(
            itemTitle = "Version",
            value = version,
            onValueChange = {version = it},
            subtitle = "The version of your application. Used for versioning your app"
        )
    }

}

@Composable
fun ProjectInfoItem(
    itemTitle: String = "",
    placeholderText: String = "",
    subtitle: String? = null,
    value: String? = "",
    onValueChange: (String) -> Unit = {}
){
    Column{
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
            value = value?:"",
            onValueChange = {it -> onValueChange(it)},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholderText) },
            shape = MaterialTheme.shapes.medium,
            supportingText = { Text(text = subtitle?:"", textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()) },
        )
    }
}
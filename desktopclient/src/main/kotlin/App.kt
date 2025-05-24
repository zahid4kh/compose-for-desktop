
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowState
import theme.AppTheme


@Composable
@Preview
fun App(
    viewModel: MainViewModel,
    window: WindowState
) {
    val uiState by viewModel.uiState.collectAsState()

    AppTheme(darkTheme = uiState.darkMode) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ){
            item{
                Header(window)
            }

            item{
                HorizontalDivider()
            }

            item{
                ProjectInformationSection()
            }

            item{
                UIConfigSection()
            }

            item{
                DependencySection()
            }

            item{
                GenerateButton()
            }
        }
    }
}


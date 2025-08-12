@file:JvmName("ComposeforDesktopWizard")
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import composefordesktop.resources.Res
import composefordesktop.resources.windowsos
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import theme.AppTheme
import java.awt.Dimension

fun main() = application {
    startKoin {
        modules(appModule)
    }

    val viewModel = getKoin().get<MainViewModel>()
    val state = rememberWindowState(
        size = DpSize(480.dp, 700.dp),
        position = WindowPosition.Aligned(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        alwaysOnTop = true,
        title = "Compose for Desktop Wizard",
        icon = painterResource(Res.drawable.windowsos),
        resizable = false
    ) {

        window.minimumSize = Dimension(480, 700)

        AppTheme{
            App(
                viewModel = viewModel,
                window = state
            )
        }
    }
}
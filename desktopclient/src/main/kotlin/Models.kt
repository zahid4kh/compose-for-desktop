import kotlinx.serialization.Serializable
import java.io.File


@Serializable
data class AppSettings(
    val darkMode: Boolean = false
)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import projectgen.*
import java.io.File
import javax.imageio.ImageIO

class MainViewModel(
    private val database: Database,
) : ViewModel() {
    private val _state = MutableStateFlow(ViewState())
    val state: StateFlow<ViewState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ViewEffect>()
    val effects: SharedFlow<ViewEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            val settings = database.getSettings()
            _state.update { it.copy(darkMode = settings.darkMode) }
        }
    }

    fun processIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.UpdateAppName -> {
                _state.update {
                    it.copy(
                        appName = intent.name,
                        suggestedFileName = intent.name.lowercase().replace(Regex("\\s+"), "-")
                    )
                }
            }
            is ViewIntent.UpdatePackageName -> {
                val error = validatePackageName(intent.name)
                _state.update { it.copy(packageName = intent.name, packageNameError = error) }
            }
            is ViewIntent.UpdateVersion -> {
                _state.update { it.copy(projectVersion = intent.version) }
            }
            is ViewIntent.UpdateWindowWidth -> {
                _state.update { it.copy(windowWidth = intent.width) }
            }
            is ViewIntent.UpdateWindowHeight -> {
                _state.update { it.copy(windowHeight = intent.height) }
            }
            is ViewIntent.ToggleDependency -> {
                _state.update { state ->
                    state.copy(
                        dependencies = state.dependencies.toMutableMap().apply {
                            this[intent.dependency] = intent.enabled
                        }
                    )
                }
            }
            is ViewIntent.SetLinuxMaintainer -> {
                _state.update { it.copy(linuxMaintainer = intent.maintainer) }
            }
            is ViewIntent.SetProjectDescription -> {
                _state.update { it.copy(appDescription = intent.description) }
            }
            is ViewIntent.ShowPreview -> {
                _state.update { it.copy(showPreview = true) }
            }
            is ViewIntent.HidePreview -> {
                _state.update { it.copy(showPreview = false) }
            }
            is ViewIntent.ShowFileSaver -> {
                _state.update { it.copy(showFileSaver = true) }
            }
            is ViewIntent.HideFileSaver -> {
                _state.update { it.copy(showFileSaver = false) }
            }
            is ViewIntent.HideSuccessDialog -> {
                _state.update { it.copy(showSuccessDialog = false) }
            }
            is ViewIntent.HideErrorDialog -> {
                _state.update { it.copy(showErrorDialog = false) }
            }
            is ViewIntent.SetSelectedIcon -> {
                viewModelScope.launch {
                    val validationError = validateIconDimensions(intent.icon)
                    _state.update {
                        it.copy(
                            attachedPngIcon = intent.icon.absolutePath,
                            iconError = validationError
                        )
                    }

                    if (validationError.isNotEmpty()) {
                        _state.update {
                            it.copy(
                                showErrorDialog = true,
                                errorMessage = validationError
                            )
                        }
                    }
                }
            }
            is ViewIntent.GenerateProject -> {
                validateAndShowFileSaver()
            }
            is ViewIntent.SaveProjectToFile -> {
                generateAndSaveProject(intent.file)
            }
            is ViewIntent.SetGenerating -> {
                _state.update { it.copy(isGenerating = intent.isGenerating) }
            }
        }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_state.value.darkMode
        _state.update { it.copy(darkMode = newDarkMode) }

        viewModelScope.launch {
            val settings = database.getSettings()
            database.saveSettings(settings.copy(darkMode = newDarkMode))
        }
    }

    private fun validatePackageName(packageName: String): String {
        return when {
            packageName.contains(" ") -> "Package name cannot contain spaces"
            packageName.contains(".") -> "Package name cannot contain dots"
            else -> ""
        }
    }

    private suspend fun validateIconDimensions(iconFile: File): String = withContext(Dispatchers.IO) {
        try {
            if (!iconFile.exists() || !iconFile.isFile) {
                return@withContext "Icon file does not exist"
            }

            if (iconFile.extension.lowercase() != "png") {
                return@withContext "Icon must be a PNG file"
            }

            val image = ImageIO.read(iconFile) ?: return@withContext "Unable to read image file"

            val width = image.width
            val height = image.height

            if (width != 512 || height != 512) {
                return@withContext "Icon must be exactly 512x512 pixels. Current size: ${width}x${height} pixels"
            }

            return@withContext ""
        } catch (e: Exception) {
            return@withContext "Error validating icon: ${e.message}"
        }
    }

    private fun validateAndShowFileSaver() {
        val state = _state.value

        if (state.appName.isBlank() || state.packageName.isBlank()) {
            _state.update {
                it.copy(
                    showErrorDialog = true,
                    errorMessage = "Please fill in all required fields (App Name and Package Name)"
                )
            }
            return
        }

        if (state.packageNameError.isNotEmpty()) {
            _state.update {
                it.copy(
                    showErrorDialog = true,
                    errorMessage = "Please fix package name errors before generating"
                )
            }
            return
        }

        if (state.attachedPngIcon.isNotEmpty() && state.iconError.isNotEmpty()) {
            _state.update {
                it.copy(
                    showErrorDialog = true,
                    errorMessage = "Please fix icon errors before generating:\n${state.iconError}"
                )
            }
            return
        }

        processIntent(ViewIntent.ShowFileSaver)
    }

    private fun generateAndSaveProject(destinationFile: File) {
        viewModelScope.launch {
            try {
                processIntent(ViewIntent.SetGenerating(true))
                processIntent(ViewIntent.HideFileSaver)

                val state = _state.value
                val generator = ProjectGenerator()

                val success = withContext(Dispatchers.IO) {
                    generator.generateProject(
                        options = ProjectOptions(
                            appName = state.appName,
                            packageName = state.packageName,
                            projectVersion = state.projectVersion,
                            windowWidth = state.windowWidth,
                            windowHeight = state.windowHeight,
                            includeRetrofit = state.dependencies["Retrofit"] ?: false,
                            includeDeskit = state.dependencies["Deskit"] ?: true,
                            includeSQLDelight = state.dependencies["SQLDelight"] ?: false,
                            includeKtor = state.dependencies["Ktor"] ?: false,
                            includeDecompose = state.dependencies["Decompose"] ?: false,
                            includeImageLoader = state.dependencies["ImageLoader"] ?: false,
                            includePrecompose = state.dependencies["Precompose"] ?: false,
                            includeSentry = state.dependencies["Sentry"] ?: false,
                            includeMarkdown = state.dependencies["Markdown"] ?: false,
                            includeKotlinxDatetime = state.dependencies["KotlinxDatetime"] ?: false,
                            linuxMaintainer = state.linuxMaintainer,
                            appDescription = state.appDescription,
                            attachedPngIcon = state.attachedPngIcon
                        ),
                        destinationFile = destinationFile
                    )
                }

                if (success) {
                    _state.update {
                        it.copy(
                            showSuccessDialog = true,
                            successMessage = "Project '${state.appName}' has been successfully generated and saved to:\n${destinationFile.absolutePath}"
                        )
                    }
                    _effects.emit(ViewEffect.ProjectGenerated(destinationFile.absolutePath))
                } else {
                    _state.update {
                        it.copy(
                            showErrorDialog = true,
                            errorMessage = "Failed to generate project. Please try again."
                        )
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        showErrorDialog = true,
                        errorMessage = "Error generating project: ${e.message}"
                    )
                }
            } finally {
                processIntent(ViewIntent.SetGenerating(false))
            }
        }
    }
}
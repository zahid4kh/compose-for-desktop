import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val database: Database,
) {
    private val _state = MutableStateFlow(ViewState())
    val state: StateFlow<ViewState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ViewEffect>()
    val effects: SharedFlow<ViewEffect> = _effects.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            val settings = database.getSettings()
            _state.update { it.copy(darkMode = settings.darkMode) }
        }
    }

    fun processIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.UpdateAppName -> {
                _state.update { it.copy(appName = intent.name) }
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
            is ViewIntent.ShowPreview -> {
                _state.update { it.copy(showPreview = true) }
            }
            is ViewIntent.HidePreview -> {
                _state.update { it.copy(showPreview = false) }
            }
            is ViewIntent.GenerateProject -> {
                generateProject()
            }
            is ViewIntent.SetGenerating -> {
                _state.update { it.copy(isGenerating = intent.isGenerating) }
            }
        }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_state.value.darkMode
        _state.update { it.copy(darkMode = newDarkMode) }

        scope.launch {
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

    private fun generateProject() {
        scope.launch {
            try {
                processIntent(ViewIntent.SetGenerating(true))
                val state = _state.value

                if (state.appName.isBlank() || state.packageName.isBlank()) {
                    _effects.emit(ViewEffect.ShowError("Please fill in all required fields"))
                    processIntent(ViewIntent.SetGenerating(false))
                    return@launch
                }

                if (state.packageNameError.isNotEmpty()) {
                    _effects.emit(ViewEffect.ShowError("Please fix package name errors"))
                    processIntent(ViewIntent.SetGenerating(false))
                    return@launch
                }

                val generator = ProjectGenerator()
                val zipPath = generator.generateProject(
                    ProjectOptions(
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
                        includeHotReload = state.dependencies["HotReload"] ?: true,
                        includeKotlinxDatetime = state.dependencies["KotlinxDatetime"] ?: false
                    )
                )

                _effects.emit(ViewEffect.ProjectGenerated(zipPath))
                processIntent(ViewIntent.SetGenerating(false))
            } catch (e: Exception) {
                _effects.emit(ViewEffect.ShowError("Error generating project: ${e.message}"))
                processIntent(ViewIntent.SetGenerating(false))
            }
        }
    }
}
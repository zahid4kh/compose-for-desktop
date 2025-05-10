// build.gradle.kts
async function addBuildGradle(folder, options) {
  let content = `
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID

plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.20"`;

  if (options.includeSQLDelight) {
    content += `
    id("app.cash.sqldelight") version "2.0.2"`;
  }
  content += `
}

group = "${options.packageName}"
version = "${options.projectVersion}"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
    implementation("io.insert-koin:koin-core:4.0.3")
`;

  if (options.includeRetrofit) {
    content += `
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
`;
  }

  if (options.includeSQLDelight) {
    content += `
    // SQLDelight for local database
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
`;
  }

  if (options.includeKtor) {
    content += `
    // Ktor client
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-cio:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
`;
  }

  if (options.includeDecompose) {
    content += `
    // Decompose for navigation
    implementation("com.arkivanov.decompose:decompose:2.2.2")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:2.2.2")
`;
  }

  if (options.includeImageLoader) {
    content += `
    // Image loading
    implementation("io.github.qdsfdhvh:image-loader:1.7.1")
`;
  }

  content += `}

compose.desktop {
    application {
        /*
        must match the annotation in Main.kt
        @file:JvmName("${options.appName}").
        This also sets the app's dock name on Linux.
         */
        mainClass = "${options.appName}"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "${options.appName.toLowerCase().replace(/\s+/g, "")}"
            packageVersion = "${options.projectVersion}"

            linux{
                shortcut = true
            }

            windows{
                shortcut = true
                dirChooser = true
                menu = true
                upgradeUuid = "run the 'generateUpgradeUuid' task and paste the generated UUID here only once"
            }

            macOS{
                dockName = "${options.appName}"
            }
        }
    }
}`;

  if (options.includeSQLDelight) {
    content += `
sqldelight {
    databases {
        create("${options.appName}") {
            packageName.set("${options.packageName}")
        }
    }
}`;
  }

tasks.register("generateUpgradeUuid") {
    group = "help"
    description = "Generates a unique UUID to be used for the Windows MSI upgradeUuid."
    doLast {
        println("--------------------------------------------------")
        println("Generated Upgrade UUID (must be pasted in the upgradeUuid for windows block only once so the MSI installer recognizes the update and does the uninstall/install):")
        println(UUID.randomUUID().toString())
        println("--------------------------------------------------")
    }
}`;

  folder.file("build.gradle.kts", content);
}

// settings.gradle.kts
async function addSettingsGradle(folder, options) {
  const content = `pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("org.jetbrains.kotlin.plugin.compose").version(extra["kotlin.version"] as String)
    }
}

rootProject.name = "${options.appName.toLowerCase().replace(/\s+/g, "")}"`;

  folder.file("settings.gradle.kts", content);
}

// Main.kt
async function addMainFile(folder, options) {
  const content = `@file:JvmName("${options.appName}")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import theme.AppTheme
import java.awt.Dimension

import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() = application {
    startKoin {
        modules(appModule)
    }

    val viewModel = getKoin().get<MainViewModel>()

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(${options.windowWidth}.dp, ${options.windowHeight}.dp)),
        title = "${options.appName} - Made with Compose for Desktop"
    ) {
        window.minimumSize = Dimension(${options.windowWidth}, ${options.windowHeight})

        AppTheme {
            App(
                viewModel = viewModel
            )
        }
    }
}`;

  folder.file("Main.kt", content);
}

// App.kt
async function addAppFile(folder, options) {
  const content = `import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import theme.AppTheme


@Composable
@Preview
fun App(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    AppTheme(darkTheme = uiState.darkMode) {
        // Your app content here
    }
}`;

  folder.file("App.kt", content);
}

// AppModule.kt
async function addAppModuleFile(folder, options) {
  const content = `import org.koin.dsl.module

val appModule = module {
    single { Database() }
    single { MainViewModel(get()) }
}`;

  folder.file("AppModule.kt", content);
}

// Database.kt
async function addDatabaseFile(folder, options) {
  const content = `import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class Database {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val appDir: File
    private val settingsFile: File

    init {
        val userHome = System.getProperty("user.home")
        appDir = File(userHome, ".${options.appName
          .toLowerCase()
          .replace(/\s+/g, "")}").apply {
            if (!exists()) mkdirs()
        }

        settingsFile = File(appDir, "settings.json")

        if (!settingsFile.exists()) settingsFile.writeText(json.encodeToString(AppSettings()))
    }

    suspend fun getSettings(): AppSettings = withContext(Dispatchers.IO) {
        return@withContext try {
            json.decodeFromString(settingsFile.readText())
        } catch (e: Exception) {
            AppSettings()
        }
    }

    suspend fun saveSettings(settings: AppSettings) = withContext(Dispatchers.IO) {
        settingsFile.writeText(json.encodeToString(settings))
    }
}`;

  folder.file("Database.kt", content);
}

// MainViewModel.kt
async function addMainViewModelFile(folder, options) {
  const content = `import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val database: Database,
) {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            val settings = database.getSettings()
            _uiState.value = _uiState.value.copy(
                darkMode = settings.darkMode,
            )
        }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.darkMode
        _uiState.value = _uiState.value.copy(darkMode = newDarkMode)

        scope.launch {
            val settings = database.getSettings()
            database.saveSettings(settings.copy(darkMode = newDarkMode))
        }
    }

    data class UiState(
        val darkMode: Boolean = false,
        val isLoading: Boolean = false
    )
}`;

  folder.file("MainViewModel.kt", content);
}

// Models.kt
async function addModelsFile(folder, options) {
  const content = `import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class AppSettings(
    val darkMode: Boolean = false
)`;

  folder.file("Models.kt", content);
}

// README.md
async function addReadmeFile(folder, options) {
  const content = `# ${options.appName}

A desktop application built with Kotlin and Compose for Desktop.

## Features

- Modern UI with Material 3 design
- Dark mode support
- Cross-platform (Windows, macOS, Linux)

## Development Setup

### Prerequisites

- JDK 17 or later
- IntelliJ IDEA (recommended) or Android Studio

### Running the Application

1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Run the \`Main.kt\` file or use the Gradle task \`run\`

### Building a Native Distribution

To build a native distribution for your platform:

\`\`\`
./gradlew packageDistributionForCurrentOS
\`\`\`

This will create a platform-specific installer in the \`build/compose/binaries/main-release/{extension}/\` directory.

## Generated with Compose for Desktop Wizard

This project was generated using the [Compose for Desktop Wizard](https://github.com/zahid4kh/compose-for-desktop).`;

  folder.file("README.md", content);
}

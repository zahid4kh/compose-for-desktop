document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("projectForm");
  const generateBtn = document.getElementById("generateBtn");
  const generatingOverlay = document.getElementById("generatingOverlay");

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    generateProject();
  });

  async function generateProject() {
    generatingOverlay.classList.remove("hidden");

    try {
      const appName = document.getElementById("appName").value.trim();
      const packageName = document.getElementById("packageName").value.trim();
      const projectVersion = document
        .getElementById("projectVersion")
        .value.trim();
      const windowWidth = document.getElementById("windowWidth").value;
      const windowHeight = document.getElementById("windowHeight").value;

      const includeRetrofit = document.getElementById("retrofit").checked;
      const includeSQLDelight = document.getElementById("sqldelight").checked;
      const includeKtor = document.getElementById("ktor").checked;
      const includeDecompose = document.getElementById("decompose").checked;
      const includeImageLoader = document.getElementById("imageLoader").checked;

      if (!appName || !packageName) {
        alert("Please fill out all required fields");
        generatingOverlay.classList.add("hidden");
        return;
      }

      const zip = new JSZip();

      await addTemplateFiles(zip, {
        appName,
        packageName,
        projectVersion,
        windowWidth,
        windowHeight,
        includeRetrofit,
        includeSQLDelight,
        includeKtor,
        includeDecompose,
        includeImageLoader,
      });

      const content = await zip.generateAsync({ type: "blob" });
      saveAs(content, `${appName.toLowerCase().replace(/\s+/g, "-")}.zip`);
    } catch (error) {
      console.error("Error generating project:", error);
      alert(
        "An error occurred while generating the project. Please try again."
      );
    } finally {
      generatingOverlay.classList.add("hidden");
    }
  }

  async function addTemplateFiles(zip, options) {
    const rootFolder = zip.folder(
      options.appName.toLowerCase().replace(/\s+/g, "-")
    );

    const gradleFolder = rootFolder.folder("gradle");
    const wrapperFolder = gradleFolder.folder("wrapper");

    const srcFolder = rootFolder.folder("src");
    const mainFolder = srcFolder.folder("main");
    const kotlinFolder = mainFolder.folder("kotlin");
    mainFolder.folder("resources");

    const testFolder = srcFolder.folder("test");
    testFolder.folder("kotlin");
    testFolder.folder("resources");

    // Add theme folder
    const themeFolder = kotlinFolder.folder("theme");

    // Add template files with replacements
    await addBuildGradle(rootFolder, options);
    await addSettingsGradle(rootFolder, options);
    await addGradleProperties(rootFolder, options);
    await addGradleWrapperFiles(wrapperFolder);
    await addGradleWrapperScripts(rootFolder);

    await addMainFile(kotlinFolder, options);
    await addAppFile(kotlinFolder, options);
    await addAppModuleFile(kotlinFolder, options);
    await addDatabaseFile(kotlinFolder, options);
    await addMainViewModelFile(kotlinFolder, options);
    await addModelsFile(kotlinFolder, options);

    // Add theme files
    await addThemeFiles(themeFolder);

    // Add README
    await addReadmeFile(rootFolder, options);
  }

  async function addBuildGradle(folder, options) {
    let content = `
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID

plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.20"
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
    implementation("app.cash.sqldelight:sqlite-driver:2.0.1")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
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
        @file:JvmName("Desktop").
        This also sets the app's dock name on Linux.
         */
        mainClass = "${options.packageName}"

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

  async function addGradleProperties(folder, options) {
    const content = `org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
kotlin.code.style=official
kotlin.version=2.1.0
compose.version=1.7.3`;

    folder.file("gradle.properties", content);
  }

  async function addGradleWrapperFiles(folder) {
    folder.file(
      "gradle-wrapper.jar",
      "// Binary JAR file also would be fetched froom zahid4kh/compose-for-desktop"
    );
    folder.file(
      "gradle-wrapper.properties",
      `distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.4-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists`
    );
  }

  async function addGradleWrapperScripts(folder) {
    folder.file(
      "gradlew",
      `#!/bin/sh
# Gradle wrapper script for Unix systems
# Content not included - would be fetched from repo`
    );

    folder.file(
      "gradlew.bat",
      `@rem
@rem Gradle wrapper script for Windows
@rem Content not included - would be fetched from repo`
    );
  }

  async function addMainFile(folder, options) {
    const content = `@file:JvmName("${options.appName}") // custom class name
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

  async function addAppFile(folder, options) {
    const content = `import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
        // app content
    }
}`;

    folder.file("App.kt", content);
  }

  async function addAppModuleFile(folder, options) {
    const content = `import org.koin.dsl.module

val appModule = module {
    single { Database() }
    single { MainViewModel(get()) }
}`;

    folder.file("AppModule.kt", content);
  }

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

  async function addModelsFile(folder, options) {
    const content = `import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class AppSettings(
    val darkMode: Boolean = false
)`;

    folder.file("Models.kt", content);
  }

  async function addThemeFiles(folder) {
    // Color.kt
    folder.file(
      "Color.kt",
      `package theme

import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF212121) // Dark Grey
val md_theme_light_onPrimary = Color(0xFFFFFFFF) // White
val md_theme_light_primaryContainer = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_light_onPrimaryContainer = Color(0xFF000000) // Black
val md_theme_light_secondary = Color(0xFF757575) // Medium Grey
val md_theme_light_onSecondary = Color(0xFFFFFFFF) // White
val md_theme_light_secondaryContainer = Color(0xFFFAFAFA) // Off White
val md_theme_light_onSecondaryContainer = Color(0xFF212121) // Dark Grey
val md_theme_light_tertiary = Color(0xFF424242) // Medium Dark Grey
val md_theme_light_onTertiary = Color(0xFFFFFFFF) // White
val md_theme_light_tertiaryContainer = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_light_onTertiaryContainer = Color(0xFF000000) // Black
val md_theme_light_error = Color(0xFFB00020) // Standard Error Red
val md_theme_light_errorContainer = Color(0xFFFCEEEE) // Lighter Error Red Background
val md_theme_light_onError = Color(0xFFFFFFFF) // White
val md_theme_light_onErrorContainer = Color(0xFF66000F) // Darker Error Red Text/Icon
val md_theme_light_background = Color(0xFFFAFAFA) // Off White
val md_theme_light_onBackground = Color(0xFF000000) // Black
val md_theme_light_surface = Color(0xFFFFFFFF) // White
val md_theme_light_onSurface = Color(0xFF000000) // Black
val md_theme_light_surfaceVariant = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_light_onSurfaceVariant = Color(0xFF424242) // Medium Dark Grey
val md_theme_light_outline = Color(0xFF757575) // Medium Grey
val md_theme_light_inverseOnSurface = Color(0xFFFAFAFA) // Off White
val md_theme_light_inverseSurface = Color(0xFF212121) // Dark Grey
val md_theme_light_inversePrimary = Color(0xFFBDBDBD) // Light Grey

val md_theme_dark_primary = Color(0xFFBDBDBD) // Light Grey
val md_theme_dark_onPrimary = Color(0xFF000000) // Black
val md_theme_dark_primaryContainer = Color(0xFF424242) // Medium Dark Grey
val md_theme_dark_onPrimaryContainer = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_dark_secondary = Color(0xFF9E9E9E) // Lighter Medium Grey
val md_theme_dark_onSecondary = Color(0xFF000000) // Black
val md_theme_dark_secondaryContainer = Color(0xFF212121) // Dark Grey
val md_theme_dark_onSecondaryContainer = Color(0xFFBDBDBD) // Light Grey
val md_theme_dark_tertiary = Color(0xFFBDBDBD) // Light Grey
val md_theme_dark_onTertiary = Color(0xFF000000) // Black
val md_theme_dark_tertiaryContainer = Color(0xFF424242) // Medium Dark Grey
val md_theme_dark_onTertiaryContainer = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_dark_error = Color(0xFFCF6679) // Standard Dark Theme Error Red
val md_theme_dark_errorContainer = Color(0xFFB00020) // Standard Error Red
val md_theme_dark_onError = Color(0xFF000000) // Black
val md_theme_dark_onErrorContainer = Color(0xFFFCEEEE) // Lighter Error Red Background
val md_theme_dark_background = Color(0xFF121212) // Very Dark Grey (Near Black)
val md_theme_dark_onBackground = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_dark_surface = Color(0xFF121212) // Very Dark Grey (Near Black)
val md_theme_dark_onSurface = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_dark_surfaceVariant = Color(0xFF424242) // Medium Dark Grey
val md_theme_dark_onSurfaceVariant = Color(0xFFBDBDBD) // Light Grey
val md_theme_dark_outline = Color(0xFF757575) // Medium Grey
val md_theme_dark_inverseOnSurface = Color(0xFF000000) // Black
val md_theme_dark_inverseSurface = Color(0xFFE0E0E0) // Very Light Grey
val md_theme_dark_inversePrimary = Color(0xFF212121) // Dark Grey`
    );

    // Theme.kt
    folder.file(
      "Theme.kt",
      `package theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}`
    );

    // Type.kt
    folder.file(
      "Type.kt",
      `package theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)`
    );
  }

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
- IntelliJ IDEA (recommended) or other IDE with Kotlin support

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
});

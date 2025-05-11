// build.gradle.kts
async function addBuildGradle(folder, options) {
  let content = `
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID`;

  if (options.includeHotReload) {
    content += `
import org.jetbrains.compose.reload.ComposeHotRun 
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag`;
  }

  content += `

plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.20"`;

  if (options.includeSQLDelight) {
    content += `
    id("app.cash.sqldelight") version "2.0.2"`;
  }

  if (options.includeHotReload) {
    content += `
    id("org.jetbrains.compose.hot-reload") version "1.0.0-alpha03"`;
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

    // Koin for dependency injection
    implementation("io.insert-koin:koin-core:4.0.3")`;

  if (options.includePrecompose) {
    content += `
    // PreCompose for navigation
    implementation("moe.tlaster:precompose:1.7.0-alpha03")
`;
  }

  if (options.includeSentry) {
    content += `
    // Sentry for error tracking
    implementation("io.sentry:sentry:8.8.0")
`;
  }

  if (options.includeMarkdown) {
    content += `
    // Markdown renderer
    implementation("com.mikepenz:multiplatform-markdown-renderer:0.32.0")
`;
  }

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
    implementation("io.ktor:ktor-client-core:3.0.3")
    implementation("io.ktor:ktor-client-cio:3.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
`;
  }

  if (options.includeDecompose) {
    content += `
    // Decompose for navigation
    implementation("com.arkivanov.decompose:decompose:3.2.2")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:2.2.3")
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
        @file:JvmName("${options.appName.replace(/\s+/g, "")}").
        This also sets the app's dock name on Linux.
         */
        mainClass = "${options.appName.replace(/\s+/g, "")}"

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
        create("${options.appName.replace(/\s+/g, "")}") {
            packageName.set("${options.packageName}")
        }
    }
}`;
  }

  if (options.includeHotReload) {
    content += `
//https://github.com/JetBrains/compose-hot-reload
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}
tasks.register<ComposeHotRun>("runHot") {
    mainClass.set("${options.appName}")
}`;
  }

  content += `
tasks.register("generateUpgradeUuid") {
    group = "help"
    description = "Generates a unique UUID to be used for the Windows MSI upgradeUuid."
    doLast {
        println("--------------------------------------------------")
        println("Generated Upgrade UUID (must be pasted in the upgradeUuid for windows block only once so the MSI installer recognizes the update and does the uninstall/install):")
        println(UUID.randomUUID().toString())
        println("--------------------------------------------------")
    }
}
`;

  folder.file("build.gradle.kts", content);
}

// settings.gradle.kts
async function addSettingsGradle(folder, options) {
  let content = `pluginManagement {
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
}`;

  if (options.includeHotReload) {
    content =
      content +
      `
plugins {
  //https://github.com/JetBrains/compose-hot-reload?tab=readme-ov-file#set-up-automatic-provisioning-of-the-jetbrains-runtime-jbr-via-gradle
  id("org.gradle.toolchains.foojay-resolver-convention").version("0.9.0")
}`;
  }

  content =
    content +
    `
rootProject.name = "${options.appName.toLowerCase().replace(/\s+/g, "")}"`;

  folder.file("settings.gradle.kts", content);
}

// Main.kt
async function addMainFile(folder, options) {
  let imports = `@file:JvmName("${options.appName}")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import theme.AppTheme
import java.awt.Dimension
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin`;

  if (options.includeHotReload) {
    imports += `
import org.jetbrains.compose.reload.DevelopmentEntryPoint`;
  }

  let mainFunction = `

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

        AppTheme {`;

  if (options.includeHotReload) {
    mainFunction += `
            DevelopmentEntryPoint {
                App(
                    viewModel = viewModel
                )
            }`;
  } else {
    mainFunction += `
            App(
                viewModel = viewModel
            )`;
  }

  mainFunction += `
        }
    }
}`;

  const content = imports + mainFunction;
  folder.file("Main.kt", content);
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

async function addGitignoreFile(folder) {
  try {
    const content = await fetchTextFile(".gitignore");
    folder.file(".gitignore", content);
    return true;
  } catch (error) {
    console.error("Error adding .gitignore file:", error);
    throw error;
  }
}

import type { ProjectOptions } from "./types";

export function generateBuildGradlePreview(options: ProjectOptions): string {
  let content = `import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID
import java.util.Scanner`;

  if (options.includeHotReload) {
    content += `
import org.jetbrains.compose.reload.ComposeHotRun 
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag`;
  }

  content += `

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)`;

  if (options.includeSQLDelight) {
    content += `
    alias(libs.plugins.sqldelight)`;
  }

  if (options.includeHotReload) {
    content += `
    alias(libs.plugins.hotReload)`;
  }

  content += `
}

group = "${options.packageName.replace(/[\s.]+/g, "")}"
version = "${options.projectVersion}"

repositories {`;

  if (options.includeDeskit) {
    content += `
    maven { url = uri("https://jitpack.io") }`;
  }
  content += `
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)

    // Koin for dependency injection
    implementation(libs.koin.core)`;

  if (options.includePrecompose) {
    content += `
    implementation(libs.precompose)`;
  }

  if (options.includeSentry) {
    content += `
    implementation(libs.sentry)`;
  }

  if (options.includeMarkdown) {
    content += `
    implementation(libs.markdownRenderer)`;
  }

  if (options.includeImageLoader) {
    content += `
    implementation(libs.imageLoader)`;
  }

  if (options.includeRetrofit) {
    content += `

    implementation(libs.bundles.retrofit)`;
  }

  if (options.includeSQLDelight) {
    content += `
    implementation(libs.bundles.sqldelight)`;
  }

  if (options.includeKtor) {
    content += `
    implementation(libs.bundles.ktorClient)`;
  }

  if (options.includeDecompose) {
    content += `
    implementation(libs.bundles.decompose)`;
  }

  if (options.includeKotlinxDatetime) {
    content += `

    // Kotlin's datetime library
    implementation(libs.kotlinx.datetime)`;
  }

  if (options.includeHotReload) {
    content += `

    // SLF4J Logging (for hot reload)
    implementation(libs.bundles.slf4j)`;
  }

  if (options.includeDeskit) {
    content += `

    // Deskit - for Material3 file chooser and information dialogs
    implementation(libs.deskit)`;
  }

  content += `
}


compose.desktop {
    application {
        /*
        must match the annotation in Main.kt
        @file:JvmName("${options.appName.replace(/\s+/g, "")}").
         */
        mainClass = "${options.appName.replace(/\s+/g, "")}"

        nativeDistributions {

            jvmArgs += listOf("-Dfile.encoding=UTF-8")
            
            buildTypes.release.proguard {
                configurationFiles.from("proguard-rules.pro")
                isEnabled = true
                obfuscate = false
                optimize = true
            }
            
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "${options.packageName
              .toLowerCase()
              .replace(/\s+/g, "")}"
            packageVersion = "${options.projectVersion}"

            linux{
                shortcut = true
                iconFile.set(project.file("icons/compose.png"))
            }

            windows{
                shortcut = true
                dirChooser = true
                menu = true
                upgradeUuid = "run the 'generateUpgradeUuid' task and paste the generated UUID here only once"
                iconFile.set(project.file("icons/compose.ico"))
            }

            macOS{
                dockName = "${options.appName}"
                iconFile.set(project.file("icons/compose.icns"))
            }
        }
    }
}`;

  if (options.includeSQLDelight) {
    content += `

sqldelight {
    databases {
        create("${options.appName.replace(/\s+/g, "")}") {
            packageName.set("${options.packageName.replace(/\s+/g, ".")}")
        }
    }
}`;
  }

  if (options.includeHotReload) {
    content += `
    
//https://github.com/JetBrains/compose-hot-reload
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
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

compose.resources{
    publicResClass = false
    packageOfResClass = "${options.packageName
      .toLowerCase()
      .replace(/\s+/g, "")}.resources"
    generateResClass = auto
}
`;

  content += `

// only for LinuxOS
val workDir = file("deb-temp")
val packageName = "${options.packageName.toLowerCase().replace(/[\s.]+/g, "")}"
val desktopRelativePath = "opt/$packageName/lib/$packageName-$packageName.desktop"
val appDislayName = "${options.appName}"
val mainClass = "${options.appName.replace(/\s+/g, "")}"

fun promptUserChoice(): String {
    println(
        """
        🧩 Which packaging task do you want to run?
        1 = packageDeb (default)
        2 = packageReleaseDeb
        """.trimIndent()
    )
    print("👉 Enter your choice [1/2]: ")

    return Scanner(System.\`in\`).nextLine().trim().ifEmpty { "1" }
}

tasks.register("addStartupWMClassToDebDynamic") {
    group = "release"
    description = "Finds .deb file, modifies .desktop with Name and StartupWMClass, and rebuilds it"

    doLast {
        val debRoot = file("build/compose/binaries")
        if (!debRoot.exists()) throw GradleException("❌ Folder not found: \${debRoot}")

        val allDebs = debRoot.walkTopDown().filter { it.isFile && it.extension == "deb" }.toList()
        if (allDebs.isEmpty()) throw GradleException("❌ No .deb files found under \${debRoot}")

        // picking the latest .deb file
        val originalDeb = allDebs.maxByOrNull { it.lastModified() }!!
        println("📦 Found deb package: \${originalDeb.relativeTo(rootDir)}")

        val modifiedDeb = File(originalDeb.parentFile, originalDeb.nameWithoutExtension + "-wm.deb")

        // cleaning up "deb-temp" folder, if exists
        if (workDir.exists()) workDir.deleteRecursively()
        workDir.mkdirs()

        // Step 1: Extracting generated debian package
        exec {
            commandLine("dpkg-deb", "-R", originalDeb.absolutePath, workDir.absolutePath)
        }

        // Step 2: Modifying the desktop entry file
        val desktopFile = File(workDir, desktopRelativePath)
        if (!desktopFile.exists()) throw GradleException("❌ .desktop file not found: \${desktopRelativePath}")

        val lines = desktopFile.readLines().toMutableList()

        // Modifying the Name field (app's display name on dock)
        var nameModified = false
        for (i in lines.indices) {
            if (lines[i].trim().startsWith("Name=")) {
                lines[i] = "Name=$appDisplayName"
                nameModified = true
                println("✅ Modified Name entry to: $appDisplayName")
                break
            }
        }

        // adding Name field if it doesn't exist
        if (!nameModified) {
            lines.add("Name=$appDisplayName")
            println("✅ Added Name entry: $appDisplayName")
        }

        // Checking StartupWMClass field
        var wmClassModified = false
        for (i in lines.indices) {
            if (lines[i].trim().startsWith("StartupWMClass=")) {
                if (lines[i] != "StartupWMClass=$mainClass") {
                    lines[i] = "StartupWMClass=$mainClass"
                    wmClassModified = true
                    println("✅ Updated StartupWMClass entry to: $mainClass")
                } else {
                    println("ℹ️ StartupWMClass already correctly set to: $mainClass")
                }
                break
            }
        }

        // Adding StartupWMClass if it doesn't exist
        if (!lines.any { it.trim().startsWith("StartupWMClass=") }) {
            lines.add("StartupWMClass=$mainClass")
            println("✅ Added StartupWMClass entry: $mainClass")
        }

        // Writing changes back to file
        desktopFile.writeText(lines.joinToString("\n"))

        // Step 3: Repackaging the debian package back
        exec {
            commandLine("dpkg-deb", "-b", workDir.absolutePath, modifiedDeb.absolutePath)
        }

        println("✅ Done: Rebuilt with Name=$appDisplayName and StartupWMClass=$mainClass -> \${modifiedDeb.name}")
    }
}


tasks.register("releaseWithWMClass") {
    group = "release"
    description = "Runs packaging task (packageDeb or packageReleaseDeb), then adds StartupWMClass"

    doLast {
        val choice = promptUserChoice()

        val packagingTask = when (choice) {
            "2" -> "packageReleaseDeb"
            else -> "packageDeb"
        }

        println("▶️ Running: \${packagingTask}")
        gradle.includedBuilds.forEach { it.task(":\${packagingTask}") } // just in case of composite builds

        exec {
            commandLine("./gradlew clean")
            commandLine("./gradlew", packagingTask)
        }

        tasks.named("addStartupWMClassToDebDynamic").get().actions.forEach { it.execute(this) }
    }
}`;

  return content;
}

export function generateVersionCatalogPreview(options: ProjectOptions): string {
  let content = `[versions]
composePlugin = "1.8.0"
kotlin = "2.1.20"
kotlinxCoroutines = "1.10.2"
kotlinxSerializationJson = "1.8.1"
koin = "4.0.3"`;

  if (options.includePrecompose) {
    content += `
precompose = "1.7.0-alpha03"`;
  }

  if (options.includeSentry) {
    content += `
sentry = "8.8.0"`;
  }

  if (options.includeMarkdown) {
    content += `
markdownRenderer = "0.32.0"`;
  }

  if (options.includeRetrofit) {
    content += `
retrofit = "2.9.0"
okhttp = "4.12.0"`;
  }

  if (options.includeSQLDelight) {
    content += `
sqldelight = "2.0.2"`;
  }

  if (options.includeKtor) {
    content += `
ktor = "3.0.3"`;
  }

  if (options.includeDecompose) {
    content += `
decompose = "3.2.2"
decomposeExtensions = "2.2.3"`;
  }

  if (options.includeImageLoader) {
    content += `
imageLoader = "1.7.1"`;
  }

  if (options.includeHotReload) {
    content += `
hotReload = "1.0.0-alpha10"`;
  }

  if (options.includeKotlinxDatetime) {
    content += `
kotlinxDatetime = "0.6.2"`;
  }

  if (options.includeHotReload) {
    content += `
slf4j = "2.0.12"`;
  }

  if (options.includeDeskit) {
    content += `
deskit = "1.2.0"`;
  }

  content += `

[libraries]
# Kotlinx
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlinxCoroutines" }
kotlinx-coroutines-swing = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-swing", version.ref = "kotlinxCoroutines" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }

# Koin
koin-core = { group = "io.insert-koin", name = "koin-core", version.ref = "koin" }`;

  if (options.includePrecompose) {
    content += `

# PreCompose
precompose = { group = "moe.tlaster", name = "precompose", version.ref = "precompose" }`;
  }

  if (options.includeSentry) {
    content += `

# Sentry
sentry = { group = "io.sentry", name = "sentry", version.ref = "sentry" }`;
  }

  if (options.includeMarkdown) {
    content += `

# Markdown Renderer
markdownRenderer = { group = "com.mikepenz", name = "multiplatform-markdown-renderer", version.ref = "markdownRenderer" }`;
  }

  if (options.includeRetrofit) {
    content += `

# Retrofit & OkHttp
retrofit-core = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converterGson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp-core = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-loggingInterceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }`;
  }

  if (options.includeKotlinxDatetime) {
    content += `

# kotlinx.datetime
kotlinx-datetime = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlinxDatetime" }`;
  }

  if (options.includeHotReload) {
    content += `

# SLF4J Logging (for hot reload)
slf4j-api = { group = "org.slf4j", name = "slf4j-api", version.ref = "slf4j" }
slf4j-simple = { group = "org.slf4j", name = "slf4j-simple", version.ref = "slf4j" }`;
  }

  if (options.includeSQLDelight) {
    content += `

# SQLDelight
sqldelight-driver = { group = "app.cash.sqldelight", name = "sqlite-driver", version.ref = "sqldelight" }
sqldelight-coroutinesExtensions = { group = "app.cash.sqldelight", name = "coroutines-extensions", version.ref = "sqldelight" }`;
  }

  if (options.includeKtor) {
    content += `

# Ktor Client
ktor-clientCore = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }
ktor-clientCio = { group = "io.ktor", name = "ktor-client-cio", version.ref = "ktor" }
ktor-clientContentNegotiation = { group = "io.ktor", name = "ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serializationKotlinxJson = { group = "io.ktor", name = "ktor-serialization-kotlinx-json", version.ref = "ktor" }`;
  }

  if (options.includeDecompose) {
    content += `

# Decompose
decompose-core = { group = "com.arkivanov.decompose", name = "decompose", version.ref = "decompose" }
decompose-extensionsComposeJetbrains = { group = "com.arkivanov.decompose", name = "extensions-compose-jetbrains", version.ref = "decomposeExtensions" }`;
  }

  if (options.includeImageLoader) {
    content += `

# Image Loader
imageLoader = { group = "io.github.qdsfdhvh", name = "image-loader", version.ref = "imageLoader" }`;
  }

  if (options.includeDeskit) {
    content += `

# Deskit
deskit = { group = "com.github.zahid4kh", name = "deskit", version.ref = "deskit" }`;
  }

  content += `

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
jetbrains-compose = { id = "org.jetbrains.compose", version.ref = "composePlugin" }
kotlin-plugin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-plugin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }`;

  if (options.includeSQLDelight) {
    content += `
sqldelight = { id = "app.cash.sqldelight", version.ref = "sqldelight" }`;
  }

  if (options.includeHotReload) {
    content += `
hotReload = { id = "org.jetbrains.compose.hot-reload", version.ref = "hotReload" }`;
  }

  // bundles
  content += `

[bundles]`;

  if (options.includeRetrofit) {
    content += `
retrofit = ["retrofit-core", "retrofit-converterGson", "okhttp-core", "okhttp-loggingInterceptor"]`;
  }

  if (options.includeSQLDelight) {
    content += `
sqldelight = ["sqldelight-driver", "sqldelight-coroutinesExtensions"]`;
  }

  if (options.includeKtor) {
    content += `
ktorClient = ["ktor-clientCore", "ktor-clientCio", "ktor-clientContentNegotiation", "ktor-serializationKotlinxJson"]`;
  }

  if (options.includeDecompose) {
    content += `
decompose = ["decompose-core", "decompose-extensionsComposeJetbrains"]`;
  }

  if (options.includeHotReload) {
    content += `
slf4j = ["slf4j-api", "slf4j-simple"]`;
  }

  return content;
}

export function generateSettingsGradlePreview(options: ProjectOptions): string {
  let content = `pluginManagement {
    repositories {`;

  if (options.includeDeskit) {
    content += `
        maven { url = uri("https://jitpack.io") }`;
  }

  content += `
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}`;

  if (options.includeHotReload) {
    content += `

plugins {
  //https://github.com/JetBrains/compose-hot-reload?tab=readme-ov-file#set-up-automatic-provisioning-of-the-jetbrains-runtime-jbr-via-gradle
  id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
}`;
  }

  content += `

rootProject.name = "${options.appName.toLowerCase().replace(/\s+/g, "")}"`;

  return content;
}

export function generateMainFilePreview(options: ProjectOptions): string {
  const imports = `@file:JvmName("${options.appName.replace(/\s+/g, "")}")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import theme.AppTheme
import java.awt.Dimension
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import ${options.packageName.toLowerCase().replace(/\s+/g, "")}.resources.*
`;

  const mainFunction = `

fun main() = application {
    startKoin {
        modules(appModule)
    }

    val viewModel = getKoin().get<MainViewModel>()

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(${options.windowWidth}.dp, ${options.windowHeight}.dp)),
        alwaysOnTop = true,
        title = "${options.appName} - Made with Compose for Desktop Wizard",
        icon = null
    ) {
        window.minimumSize = Dimension(${options.windowWidth}, ${options.windowHeight})

        AppTheme {
            App(
                viewModel = viewModel
            )
        }
    }
}`;

  return imports + mainFunction;
}

export function generateReadmePreview(options: ProjectOptions): string {
  let content = `# ${options.appName}

A desktop application built with Kotlin and Compose for Desktop.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg?logo=kotlin)](https://kotlinlang.org) [![Compose](https://img.shields.io/badge/Compose-1.8.0-blue.svg?logo=jetpack-compose)](https://www.jetbrains.com/lp/compose-multiplatform/)

## Features

- Modern UI with Material 3 design
- Dark mode support
- Cross-platform (Windows, macOS, Linux)`;

  if (options.includeHotReload) {
    content += `
- Hot reload support for faster development`;
  }

  content += `

## Development Setup

### Prerequisites

- JDK 17 or later
- Kotlin 2.1.20 or later
- IntelliJ IDEA (recommended) or Android Studio

### Make Gradle Wrapper Executable (Linux/macOS only)

After cloning the repository, you need to make the Gradle wrapper executable:

\`\`\`bash
chmod +x gradlew\`\`\`

**Note:** This step is not required on Windows as it uses \`gradlew.bat\`.

### Running the Application

#### Standard Run
\`\`\`bash
./gradlew run\`\`\``;

  if (options.includeHotReload) {
    content += `

#### Hot Reload (Recommended for Development)
\`\`\`bash
./gradlew :runHot --mainClass ${options.appName.replace(
      /\s+/g,
      ""
    )} --auto\`\`\`

This enables automatic recompilation and hot swapping when you modify your code, making development much faster.`;
  }

  content += `

### Building a Native Distribution

To build a native distribution for your platform:

\`\`\`bash
./gradlew packageDistributionForCurrentOS\`\`\`

This will create a platform-specific installer in the \`build/compose/binaries/main-release/{extension}/\` directory.

### Available Gradle Tasks

- \`./gradlew run\` - Run the application`;

  if (options.includeHotReload) {
    content += `
- \`./gradlew :runHot --mainClass ${options.appName.replace(
      /\s+/g,
      ""
    )} --auto\` - Run with hot reload`;
  }

  content += `
- \`./gradlew packageDistributionForCurrentOS\` - Build native distribution for current OS
- \`./gradlew packageDmg\` - Build macOS DMG (macOS only)
- \`./gradlew packageMsi\` - Build Windows MSI (Windows only)
- \`./gradlew packageExe\` - Build Windows EXE (Windows only)
- \`./gradlew packageDeb\` - Build Linux DEB (Linux only)


## Generated with Compose for Desktop Wizard

This project was generated using the [Compose for Desktop Wizard](https://github.com/zahid4kh/compose-for-desktop).`;

  return content;
}

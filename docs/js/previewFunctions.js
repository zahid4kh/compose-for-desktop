function generateBuildGradlePreview(options) {
  let content = `import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID`;

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

  content += `
}


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
    mainClass.set("${options.appName.replace(/\s+/g, "")}")
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

  return content;
}

function generateVersionCatalogPreview(options) {
  let content = `[versions]
composePlugin = "1.7.3"
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
hotReload = "1.0.0-alpha03"`;
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

  return content;
}

function generateSettingsGradlePreview(options) {
  let content = `pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
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

  return content;
}

function generateMainFilePreview(options) {
  let imports = `@file:JvmName("${options.appName.replace(
    /\s+/g,
    ""
  )}")  // Must have no spaces!!!
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
        title = "${options.appName} - Made with Compose for Desktop Wizard"
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
  return content;
}

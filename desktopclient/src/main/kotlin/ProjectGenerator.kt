import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class ProjectGenerator {

    suspend fun generateProject(options: ProjectOptions): String = withContext(Dispatchers.IO) {
        val appNameFormatted = options.appName.lowercase().replace(Regex("\\s+"), "-")
        val destinationPath = chooseDestination(appNameFormatted)
        val tempDir = Files.createTempDirectory("compose-desktop-").toFile()

        try {
            val rootDir = File(tempDir, appNameFormatted)
            rootDir.mkdirs()

            // dirs
            val gradleDir = File(rootDir, "gradle")
            val wrapperDir = File(gradleDir, "wrapper")
            val iconsDir = File(rootDir, "icons")
            val srcDir = File(rootDir, "src")
            val mainDir = File(srcDir, "main")
            val kotlinDir = File(mainDir, "kotlin")
            val themeDir = File(kotlinDir, "theme")
            val resourcesDir = File(mainDir, "resources")
            val composeResourcesDir = File(mainDir, "composeResources")
            val drawableDir = File(composeResourcesDir, "drawable")

            listOf(gradleDir, wrapperDir, iconsDir, srcDir, mainDir, kotlinDir,
                themeDir, resourcesDir, composeResourcesDir, drawableDir).forEach { it.mkdirs() }

            // Generate and write text files
            writeTextFile(File(rootDir, "build.gradle.kts"), PreviewFunctions.generateBuildGradlePreview(options))
            writeTextFile(File(rootDir, "settings.gradle.kts"), PreviewFunctions.generateSettingsGradlePreview(options))
            writeTextFile(File(gradleDir, "libs.versions.toml"), PreviewFunctions.generateVersionCatalogPreview(options))
            writeTextFile(File(kotlinDir, "Main.kt"), PreviewFunctions.generateMainFilePreview(options))
            writeTextFile(File(rootDir, "README.md"), PreviewFunctions.generateReadmePreview(options))

            // Fetch and write static files
            fetchAndWriteTextFile("gradle/wrapper/gradle-wrapper.properties", File(wrapperDir, "gradle-wrapper.properties"))
            fetchAndWriteBinaryFile("gradle/wrapper/gradle-wrapper.jar", File(wrapperDir, "gradle-wrapper.jar"))
            fetchAndWriteTextFile("gradlew", File(rootDir, "gradlew"))
            fetchAndWriteTextFile("gradlew.bat", File(rootDir, "gradlew.bat"))
            fetchAndWriteTextFile("gradle.properties", File(rootDir, "gradle.properties"))
            fetchAndWriteTextFile(".gitignore", File(rootDir, ".gitignore"))

            // Kotlin files
            fetchAndWriteTextFile("src/main/kotlin/App.kt", File(kotlinDir, "App.kt"))
            fetchAndWriteTextFile("src/main/kotlin/AppModule.kt", File(kotlinDir, "AppModule.kt"))
            fetchAndWriteTextFile("src/main/kotlin/MainViewModel.kt", File(kotlinDir, "MainViewModel.kt"))
            fetchAndWriteTextFile("src/main/kotlin/Models.kt", File(kotlinDir, "Models.kt"))

            // Theme files
            fetchAndWriteTextFile("src/main/kotlin/theme/Color.kt", File(themeDir, "Color.kt"))
            fetchAndWriteTextFile("src/main/kotlin/theme/Theme.kt", File(themeDir, "Theme.kt"))
            fetchAndWriteTextFile("src/main/kotlin/theme/Type.kt", File(themeDir, "Type.kt"))

            // Icon files
            fetchAndWriteBinaryFile("icons/compose.png", File(iconsDir, "compose.png"))
            fetchAndWriteBinaryFile("icons/compose.ico", File(iconsDir, "compose.ico"))
            fetchAndWriteBinaryFile("icons/compose.icns", File(iconsDir, "compose.icns"))

            // Database.kt with template
            writeDatabaseFile(kotlinDir, options)

            // Proguard rules
            writeProguardRules(rootDir)

            // Create ZIP file
            createZipFile(rootDir, destinationPath)

            return@withContext destinationPath.absolutePath

        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun chooseDestination(defaultName: String): File {
        val fileChooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            val downloadsDir = File(System.getProperty("user.home"), "Downloads")
            selectedFile = File(downloadsDir, "$defaultName.zip")
            fileFilter = FileNameExtensionFilter("ZIP files", "zip")
        }

        return if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            if (file.extension != "zip") {
                File(file.absolutePath + ".zip")
            } else {
                file
            }
        } else {
            val downloadsDir = File(System.getProperty("user.home"), "Downloads")
            File(downloadsDir, "$defaultName.zip")
        }
    }

    private fun writeTextFile(file: File, content: String) {
        file.writeText(content)
    }

    private suspend fun fetchAndWriteTextFile(path: String, destination: File) = withContext(Dispatchers.IO) {
        val url = URL("https://raw.githubusercontent.com/zahid4kh/compose-for-desktop/main/$path")
        url.readText().let { destination.writeText(it) }
        if (path.endsWith("gradlew") && !System.getProperty("os.name").lowercase().contains("windows")) {
            try {
                destination.setExecutable(true)
            } catch (e: Exception) {
                println("Warning: Failed to set executable permission on ${destination.absolutePath}")
                println(e.message)
            }
        }

    }

    private suspend fun fetchAndWriteBinaryFile(path: String, destination: File) = withContext(Dispatchers.IO) {
        val url = URL("https://raw.githubusercontent.com/zahid4kh/compose-for-desktop/main/$path")
        url.openStream().use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun writeDatabaseFile(kotlinDir: File, options: ProjectOptions) {
        val content = """import kotlinx.coroutines.Dispatchers
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
        appDir = File(userHome, ".${options.appName.lowercase().replace(Regex("\\s+"), "")}").apply {
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
}"""

        File(kotlinDir, "Database.kt").writeText(content)
    }

    private fun writeProguardRules(rootDir: File) {
        val content = """-dontwarn kotlinx.serialization.**

-dontwarn sun.font.CFont
-dontwarn sun.swing.SwingUtilities2${'$'}AATextInfo
-dontwarn net.miginfocom.swing.MigLayout

-dontnote kotlinx.serialization.**
-dontnote META-INF.**
-dontnote kotlinx.serialization.internal.PlatformKt

# Keep Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep all serializable classes with their @Serializable annotation
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep serializers
-keepclasseswithmembers class **${'$'}${'$'}serializer {
    static **${'$'}${'$'}serializer INSTANCE;
}


# Keep serializable classes and their properties
-if @kotlinx.serialization.Serializable class **
-keep class <1> {
    static <1>${'$'}Companion Companion;
}

# Keep specific serializer classes
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serialization descriptors
-keep class kotlinx.serialization.descriptors.** { *; }

# Specifically keep AppSettings and its serializer
-keep class AppSettings { *; }
-keep class AppSettings${'$'}${'$'}serializer { *; }"""

        File(rootDir, "proguard-rules.pro").writeText(content)
    }

    private fun createZipFile(sourceDir: File, destination: File) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(destination))).use { zipOut ->
            sourceDir.walkTopDown().forEach { file ->
                val relativePath = file.relativeTo(sourceDir).path
                
                if (file == sourceDir) {
                    return@forEach
                }
                
                val entryPath = relativePath.replace("\\", "/")
                
                if (file.isDirectory) {
                    val directoryEntryPath = if (entryPath.endsWith("/")) entryPath else "$entryPath/"
                    zipOut.putNextEntry(ZipEntry(directoryEntryPath))
                } else {
                    zipOut.putNextEntry(ZipEntry(entryPath))
                    file.inputStream().use { it.copyTo(zipOut) }
                }
            }
        }
    }
}
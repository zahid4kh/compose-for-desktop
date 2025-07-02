import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.zip.UnixStat
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import tobegenerated.PreviewFunctions
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

class ProjectGenerator {

    suspend fun generateProject(options: ProjectOptions, destinationFile: File): Boolean = withContext(Dispatchers.IO) {
        val appNameFormatted = options.appName.lowercase().replace(Regex("\\s+"), "-")
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

            // Read and write static files from resources
            readResourceBinaryFile("/tobegenerated/binaries/gradle-wrapper.properties", File(wrapperDir, "gradle-wrapper.properties"))
            readResourceBinaryFile("/tobegenerated/binaries/gradle-wrapper.jar", File(wrapperDir, "gradle-wrapper.jar"))
            readResourceTextFile("/tobegenerated/textfiles/gradlew", File(rootDir, "gradlew"))
            readResourceTextFile("/tobegenerated/textfiles/gradlewbat", File(rootDir, "gradlew.bat"))
            readResourceTextFile("/tobegenerated/textfiles/gradleproperties", File(rootDir, "gradle.properties"))
            readResourceTextFile("/tobegenerated/textfiles/gitignore", File(rootDir, ".gitignore"))

            // Kotlin files
            readResourceTextFile("/tobegenerated/textfiles/App", File(kotlinDir, "App.kt"))
            readResourceTextFile("/tobegenerated/textfiles/AppModule", File(kotlinDir, "AppModule.kt"))
            readResourceTextFile("/tobegenerated/textfiles/MainViewModel", File(kotlinDir, "MainViewModel.kt"))
            readResourceTextFile("/tobegenerated/textfiles/Models", File(kotlinDir, "Models.kt"))

            // Theme files
            readResourceTextFile("/tobegenerated/textfiles/Color", File(themeDir, "Color.kt"))
            readResourceTextFile("/tobegenerated/textfiles/Theme", File(themeDir, "Theme.kt"))
            /*
            already handling typography from Theme
            readResourceTextFile("/tobegenerated/textfiles/Type", File(themeDir, "Type.kt"))
             */

            // Icon files
            readResourceBinaryFile("/tobegenerated/images/compose.png", File(iconsDir, "compose.png"))
            readResourceBinaryFile("/tobegenerated/images/compose.ico", File(iconsDir, "compose.ico"))
            readResourceBinaryFile("/tobegenerated/images/compose.icns", File(iconsDir, "compose.icns"))

            // Database.kt with template
            writeDatabaseFile(kotlinDir, options)

            // Proguard rules
            readResourceTextFile("/tobegenerated/textfiles/Proguard", File(rootDir, "proguard-rules.pro"))

            // Make gradlew executable on Unix-like systems
            val gradlewFile = File(rootDir, "gradlew")
            if (!System.getProperty("os.name").lowercase().contains("windows")) {
                try {
                    gradlewFile.setExecutable(true)
                } catch (e: Exception) {
                    println("Warning: Failed to set executable permission on ${gradlewFile.absolutePath}")
                }
            }

            // Create ZIP file
            createZipFile(rootDir, destinationFile)

            return@withContext true

        } catch (e: Exception) {
            println("Error generating project: ${e.message}")
            e.printStackTrace()
            return@withContext false
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun writeTextFile(file: File, content: String) {
        file.writeText(content)
    }

    private fun readResourceTextFile(resourcePath: String, destination: File) {
        val inputStream = this::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")

        inputStream.use { stream ->
            val content = stream.bufferedReader().readText()
            destination.writeText(content)
        }
    }

    private fun readResourceBinaryFile(resourcePath: String, destination: File) {
        val inputStream = this::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")

        inputStream.use { input ->
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

    private fun createZipFile(sourceDir: File, destination: File) {
        ZipArchiveOutputStream(BufferedOutputStream(FileOutputStream(destination))).use { zipOut ->
            zipOut.setLevel(9)

            sourceDir.walkTopDown().forEach { file ->
                val relativePath = file.relativeTo(sourceDir.parentFile).path
                val entryPath = relativePath.replace("\\", "/")

                val entry = ZipArchiveEntry(file, entryPath)

                if (file.name == "gradlew" && file.isFile) {
                    entry.unixMode = UnixStat.FILE_FLAG or 493 // for 0755 octal
                }

                zipOut.putArchiveEntry(entry)

                if (file.isFile) {
                    file.inputStream().use { it.copyTo(zipOut) }
                }

                zipOut.closeArchiveEntry()
            }
        }
    }
}
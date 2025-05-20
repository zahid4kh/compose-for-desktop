import JSZip from "jszip";
import type { ProjectOptions } from "./types";
import {
  generateBuildGradlePreview,
  generateSettingsGradlePreview,
  generateMainFilePreview,
  generateVersionCatalogPreview,
  generateReadmePreview,
} from "./preview-functions";

async function fetchTextFile(path: string): Promise<string> {
  try {
    const response = await fetch(
      `https://raw.githubusercontent.com/zahid4kh/compose-for-desktop/main/${path}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch ${path}: ${response.status}`);
    }
    return await response.text();
  } catch (error) {
    console.error(`Error fetching ${path}:`, error);
    throw error;
  }
}

async function fetchBinaryFile(path: string): Promise<ArrayBuffer> {
  try {
    const response = await fetch(
      `https://raw.githubusercontent.com/zahid4kh/compose-for-desktop/main/${path}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch ${path}: ${response.status}`);
    }
    return await response.arrayBuffer();
  } catch (error) {
    console.error(`Error fetching binary file ${path}:`, error);
    throw error;
  }
}

async function addGradleWrapperFiles(folder: JSZip): Promise<boolean> {
  try {
    // gradle-wrapper.properties
    const propertiesContent = await fetchTextFile(
      "gradle/wrapper/gradle-wrapper.properties"
    );
    folder.file("gradle-wrapper.properties", propertiesContent);

    // gradle-wrapper.jar (binary file)
    const jarContent = await fetchBinaryFile(
      "gradle/wrapper/gradle-wrapper.jar"
    );
    folder.file("gradle-wrapper.jar", jarContent);

    return true;
  } catch (error) {
    console.error("Error adding Gradle wrapper files:", error);
    throw error;
  }
}

async function addGradleWrapperScripts(folder: JSZip): Promise<boolean> {
  try {
    // gradlew (shell script)
    const gradlewContent = await fetchTextFile("gradlew");
    folder.file("gradlew", gradlewContent);

    // gradlew.bat (batch script)
    const gradlewBatContent = await fetchTextFile("gradlew.bat");
    folder.file("gradlew.bat", gradlewBatContent);

    return true;
  } catch (error) {
    console.error("Error adding Gradle wrapper scripts:", error);
    throw error;
  }
}

async function addGradleProperties(folder: JSZip): Promise<boolean> {
  try {
    const content = await fetchTextFile("gradle.properties");
    folder.file("gradle.properties", content);
    return true;
  } catch (error) {
    console.error("Error adding gradle.properties:", error);
    throw error;
  }
}

async function addKotlinFiles(folder: JSZip): Promise<boolean> {
  try {
    // App.kt
    const appContent = await fetchTextFile("src/main/kotlin/App.kt");
    folder.file("App.kt", appContent);

    // AppModule.kt
    const appModuleContent = await fetchTextFile(
      "src/main/kotlin/AppModule.kt"
    );
    folder.file("AppModule.kt", appModuleContent);

    // MainViewModel.kt
    const viewModelContent = await fetchTextFile(
      "src/main/kotlin/MainViewModel.kt"
    );
    folder.file("MainViewModel.kt", viewModelContent);

    // Models.kt
    const modelsContent = await fetchTextFile("src/main/kotlin/Models.kt");
    folder.file("Models.kt", modelsContent);

    return true;
  } catch (error) {
    console.error("Error adding Kotlin files:", error);
    throw error;
  }
}

async function addThemeFiles(folder: JSZip): Promise<boolean> {
  try {
    // Color.kt
    const colorContent = await fetchTextFile("src/main/kotlin/theme/Color.kt");
    folder.file("Color.kt", colorContent);

    // Theme.kt
    const themeContent = await fetchTextFile("src/main/kotlin/theme/Theme.kt");
    folder.file("Theme.kt", themeContent);

    // Type.kt
    const typeContent = await fetchTextFile("src/main/kotlin/theme/Type.kt");
    folder.file("Type.kt", typeContent);

    return true;
  } catch (error) {
    console.error("Error adding theme files:", error);
    throw error;
  }
}

async function addIconFiles(folder: JSZip): Promise<boolean> {
  try {
    // compose.png
    const pngContent = await fetchBinaryFile("icons/compose.png");
    folder.file("compose.png", pngContent);

    // compose.ico
    const icoContent = await fetchBinaryFile("icons/compose.ico");
    folder.file("compose.ico", icoContent);

    // compose.icns
    const icnsContent = await fetchBinaryFile("icons/compose.icns");
    folder.file("compose.icns", icnsContent);

    return true;
  } catch (error) {
    console.error("Error adding icon files:", error);
    throw error;
  }
}

async function addGitignoreFile(folder: JSZip): Promise<boolean> {
  try {
    const content = await fetchTextFile(".gitignore");
    folder.file(".gitignore", content);
    return true;
  } catch (error) {
    console.error("Error adding .gitignore file:", error);
    throw error;
  }
}

async function addDatabaseFile(
  folder: JSZip,
  options: { appName: string }
): Promise<void> {
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

export async function generateProject(options: ProjectOptions) {
  try {
    console.log("Starting project generation...", options);

    const zip = new JSZip();

    const appNameFormatted = options.appName.toLowerCase().replace(/\s+/g, "-");
    console.log("Formatted app name:", appNameFormatted);

    const rootFolder = zip.folder(appNameFormatted);
    if (!rootFolder) {
      throw new Error("Failed to create root folder");
    }

    console.log("Creating project structure...");

    // folder structure
    const gradleFolder = rootFolder.folder("gradle");
    if (!gradleFolder) {
      throw new Error("Failed to create gradle folder");
    }

    const wrapperFolder = gradleFolder.folder("wrapper");
    if (!wrapperFolder) {
      throw new Error("Failed to create wrapper folder");
    }

    const iconsFolder = rootFolder.folder("icons");
    if (!iconsFolder) {
      throw new Error("Failed to create icons folder");
    }

    const srcFolder = rootFolder.folder("src");
    if (!srcFolder) {
      throw new Error("Failed to create src folder");
    }

    const mainFolder = srcFolder.folder("main");
    if (!mainFolder) {
      throw new Error("Failed to create main folder");
    }

    const composeResourcesFolder = mainFolder.folder("composeResources");
    if (!composeResourcesFolder) {
      throw new Error("Failed to create composeResources folder");
    }

    const drawableFolder = composeResourcesFolder.folder("drawable");
    if (!drawableFolder) {
      throw new Error("Failed to create drawable folder");
    }

    const kotlinFolder = mainFolder.folder("kotlin");
    if (!kotlinFolder) {
      throw new Error("Failed to create kotlin folder");
    }

    const themeFolder = kotlinFolder.folder("theme");
    if (!themeFolder) {
      throw new Error("Failed to create theme folder");
    }

    mainFolder.folder("resources");

    const testFolder = srcFolder.folder("test");
    if (testFolder) {
      testFolder.folder("kotlin");
      testFolder.folder("resources");
    }

    console.log("Adding Gradle files...");
    // Add Gradle files
    await addGradleWrapperFiles(wrapperFolder);
    await addGradleWrapperScripts(rootFolder);
    await addGradleProperties(rootFolder);

    // Add version catalog
    const versionCatalog = generateVersionCatalogPreview(options);
    gradleFolder.file("libs.versions.toml", versionCatalog);
    console.log("Version catalog generated");

    // Add build.gradle.kts
    const buildGradle = generateBuildGradlePreview(options);
    rootFolder.file("build.gradle.kts", buildGradle);
    console.log("Build gradle generated");

    // Add settings.gradle.kts
    const settingsGradle = generateSettingsGradlePreview(options);
    rootFolder.file("settings.gradle.kts", settingsGradle);
    console.log("Settings gradle generated");

    console.log("Adding Icon files...");
    // Add icon files
    await addIconFiles(iconsFolder);

    console.log("Adding Kotlin files...");
    // Add Main.kt
    const mainKt = generateMainFilePreview(options);
    kotlinFolder.file("Main.kt", mainKt);
    console.log("Main.kt generated");

    // Add Database.kt
    await addDatabaseFile(kotlinFolder, { appName: options.appName });
    console.log("Database.kt generated");

    // Add other Kotlin files
    await addKotlinFiles(kotlinFolder);
    console.log("Additional Kotlin files generated");

    // Add theme files
    await addThemeFiles(themeFolder);
    console.log("Theme files generated");

    // Add README.md
    const readme = generateReadmePreview(options);
    rootFolder.file("README.md", readme);
    console.log("README.md generated");

    // Add .gitignore
    await addGitignoreFile(rootFolder);
    console.log(".gitignore generated");

    console.log("Generating ZIP file...");

    const content = await zip.generateAsync({
      type: "blob",
      compression: "DEFLATE",
      compressionOptions: { level: 9 },
    });
    console.log("ZIP file generated, size:", content.size);

    console.log("Creating download link...");
    const url = URL.createObjectURL(content);
    const link = document.createElement("a");
    link.href = url;
    link.download = `${appNameFormatted}.zip`;

    document.body.appendChild(link);
    console.log("Triggering download...");

    setTimeout(() => {
      link.click();

      console.log("Cleaning up...");
      setTimeout(() => {
        URL.revokeObjectURL(url);
        document.body.removeChild(link);
        console.log("Download process completed");
      }, 200);
    }, 100);

    return true;
  } catch (error) {
    console.error("Error generating project:", error);
    alert(
      `Error generating project: ${
        error instanceof Error ? error.message : String(error)
      }`
    );
    throw error;
  }
}

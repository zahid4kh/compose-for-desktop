import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "desktop"
version = "1.0.0"

repositories {
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
    implementation(libs.koin.core)
}

compose.desktop {
    application {
        /*
        Must match the annotation in Main.kt
        @file:JvmName("Compose for Desktop Wizard").
        This also sets the app's dock name on Linux.
         */
        mainClass = "Compose for Desktop Wizard"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "composedesktop"
            packageVersion = "1.0.0"

            linux{
                shortcut = true
                iconFile.set(project.file("src/main/composeResources/drawable/linuxos.png"))
            }

            windows{
                shortcut = true
                dirChooser = true
                menu = true
                upgradeUuid = "run the 'generateUpgradeUuid' task and paste the generated UUID here only once. Must be done before packaging the app for windows"
                iconFile.set(project.file("src/main/composeResources/drawable/windowsos.ico"))
            }

            macOS{
                dockName = "Compose for Desktop Wizard"
                iconFile.set(project.file("src/main/composeResources/drawable/macos.icns"))
            }
        }
    }
}

compose.resources{
    publicResClass = false
    packageOfResClass = "desktop.resources"
    generateResClass = auto
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
}

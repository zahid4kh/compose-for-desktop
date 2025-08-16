
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import java.util.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.hotReload) apply false
}

group = "desktopclient"
version = "1.0.1"

val isReleaseBuild = gradle.startParameter.taskNames.any {
    it.contains("release", ignoreCase = true) || it.contains("buildUberDeb")
}
if (!isReleaseBuild) {
    apply(plugin = "org.jetbrains.compose.hot-reload")
}

repositories {
    maven { url = uri("https://jitpack.io") }
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

    if (!isReleaseBuild) {
        implementation(libs.bundles.slf4j)
    } else {
        implementation(libs.slf4j.nop)
    }

    implementation(libs.deskit)
    implementation(libs.apache.commons.compress)
    implementation(libs.twelvemonkeys.imageio.icns)
    implementation(libs.apache.commons.imaging)

}

compose.desktop {
    application {
        /*
        Must match the annotation in Main.kt
        @file:JvmName("Compose for Desktop Wizard").
        This also sets the app's dock name on Linux.
         */
        mainClass = "ComposeforDesktopWizard"

        nativeDistributions {
            jvmArgs += listOf("-Dfile.encoding=UTF-8")

            buildTypes.release.proguard {
                configurationFiles.from("proguard-rules.pro")
                isEnabled = true
                obfuscate = false
                optimize = true
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "composefordesktop"
            packageVersion = "1.0.1"

            linux{
                shortcut = true
                iconFile.set(project.file("icons/composedesktop.png"))
                description = "Desktop client for Compose for Desktop Wizard"
            }

            windows{
                shortcut = true
                dirChooser = true
                menu = true
                upgradeUuid = "ac6acb55-557f-4666-bbd6-ff0521dc2279"
                iconFile.set(project.file("icons/composedesktop.ico"))
            }

            macOS{
                dockName = "Compose for Desktop Wizard"
                iconFile.set(project.file("icons/composedesktop.icns"))
            }
        }
    }
}

compose.resources{
    publicResClass = false
    packageOfResClass = "composefordesktop.resources"
    generateResClass = auto
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
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



// only for LinuxOS
val packageName = "${compose.desktop.application.nativeDistributions.packageName}"
val appDisplayName = "Compose for Desktop Wizard"
val mainClass = "${compose.desktop.application.mainClass}"
val maintainer = "Zahid Khalilov <halilzahid@gmail.com>"
val controlDescription = "Compose for Desktop Wizard provides a friendly user interface for creating and managing Compose for Desktop applications. It includes features for project setup, UI configuration management, and build automation."

tasks.register<Exec>("buildUberDeb") {
    group = "release"
    description = "Builds a lean .deb package from the optimized uber-JAR. It automatically runs the 'packageReleaseUberJarForCurrentOS' task first, so there is no need to run it separately. The task then creates a standard Debian file structure with a launcher script, a .desktop file for application menus, and install/remove scripts for clean system integration."

    dependsOn(tasks.named("packageReleaseUberJarForCurrentOS"))

    val debianRoot = project.layout.buildDirectory.dir("debian/$packageName-${project.version}")
    val finalDebDir = project.layout.buildDirectory.dir("dist")

    val uberJar = tasks.named<AbstractArchiveTask>("packageReleaseUberJarForCurrentOS").flatMap { it.archiveFile }
    inputs.file(uberJar)
    outputs.dir(finalDebDir)

    doFirst {
        delete(debianRoot)
        mkdir(debianRoot)

        val controlFile = file("${debianRoot.get()}/DEBIAN/control")
        controlFile.parentFile.mkdirs()
        controlFile.writeText("""
            Package: $packageName
            Version: ${project.version}
            Architecture: all
            Maintainer: $maintainer
            Depends: default-jre | java17-runtime | openjdk-17-jre
            Description: $controlDescription
            
        """.trimIndent())

        val binDir = file("${debianRoot.get()}/opt/$packageName/bin")
        binDir.mkdirs()
        val launcherScript = file("$binDir/$packageName")
        launcherScript.writeText("""
            #!/bin/sh
            echo "Launching $appDisplayName..."
            exec java -jar /opt/$packageName/lib/$packageName.jar "$@"
        """.trimIndent())
        launcherScript.setExecutable(true, false)

        val libDir = file("${debianRoot.get()}/opt/$packageName/lib")
        libDir.mkdirs()
        copy {
            from(uberJar)
            into(libDir)
            rename { "$packageName.jar" }
        }

        val desktopFileDir = file("${debianRoot.get()}/usr/share/applications")
        desktopFileDir.mkdirs()
        file("$desktopFileDir/$packageName.desktop").writeText("""
            [Desktop Entry]
            Version=1.0
            Name=$appDisplayName
            Comment=$controlDescription
            Exec=/opt/$packageName/bin/$packageName
            Icon=$packageName
            Terminal=false
            Type=Application
            Categories=Development;IDE;
            StartupWMClass=$mainClass
        """.trimIndent())

        val iconPath = "icons/composedesktop.png"
        val iconDir = file("${debianRoot.get()}/usr/share/icons/hicolor/512x512/apps")
        iconDir.mkdirs()
        copy {
            from(iconPath)
            into(iconDir)
            rename { "$packageName.png" }
        }

        val postinstFile = file("${debianRoot.get()}/DEBIAN/postinst")
        postinstFile.writeText("""
            #!/bin/sh
            set -e
            echo "Creating symlink for terminal access..."
            ln -sf /opt/$packageName/bin/$packageName /usr/local/bin/$packageName
            echo "Symlink created: /usr/local/bin/$packageName"
            
            echo "Updating icon cache..."
            gtk-update-icon-cache -q /usr/share/icons/hicolor || true
            
            echo "Updating desktop database..."
            update-desktop-database -q /usr/share/applications || true
            
            echo "Installation of '$appDisplayName' complete."
            exit 0
        """.trimIndent())
        postinstFile.setExecutable(true, false)

        val prermFile = file("${debianRoot.get()}/DEBIAN/prerm")
        prermFile.writeText("""
            #!/bin/sh
            set -e
            echo "Removing symlink: /usr/local/bin/$packageName"
            rm -f /usr/local/bin/$packageName
            echo "Symlink removed."
            echo "Pre-removal steps for '$appDisplayName' complete."
            exit 0
        """.trimIndent())
        prermFile.setExecutable(true, false)
    }

    workingDir(debianRoot.get().asFile.parentFile)
    commandLine("dpkg-deb", "--build", "--root-owner-group", debianRoot.get().asFile.name, finalDebDir.get().asFile.path)
}
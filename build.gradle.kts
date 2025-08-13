
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import java.util.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.hotReload)
}

group = "desktopclient"
version = "1.0.0"

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
    implementation(libs.bundles.slf4j)

    implementation("com.github.zahid4kh:deskit:1.4.0")
    implementation("org.apache.commons:commons-compress:1.28.0")

    implementation("com.twelvemonkeys.imageio:imageio-icns:3.12.0")
    implementation("org.apache.commons:commons-imaging:1.0.0-alpha6")

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
            packageVersion = "1.0.0"

            linux{
                shortcut = true
                iconFile.set(project.file("src/main/composeResources/drawable/composedesktop.png"))
                description = "Desktop client for Compose for Desktop Wizard"
            }

            windows{
                shortcut = true
                dirChooser = true
                menu = true
                upgradeUuid = null
                iconFile.set(project.file("src/main/composeResources/drawable/composedesktop.ico"))
            }

            macOS{
                dockName = "Compose for Desktop Wizard"
                iconFile.set(project.file("src/main/composeResources/drawable/composedesktop.icns"))
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
val workDir = file("deb-temp")
val packageName = "${compose.desktop.application.nativeDistributions.packageName}"
val desktopRelativePath = "opt/$packageName/lib/$packageName-$packageName.desktop"
val appDisplayName = "Compose for Desktop Wizard"
val mainClass = "${compose.desktop.application.mainClass}"
val maintainer = "Zahid Khalilov <halilzahid@gmail.com>"
val controlDescription = "Compose for Desktop Wizard provides a friendly user interface for creating and managing Compose for Desktop applications. It includes features for project setup, UI configuration management, and build automation."

fun promptUserChoice(): String {
    println(
        """
        🧩 Which packaging task do you want to run?
        1 = packageDeb (default)
        2 = packageReleaseDeb
        """.trimIndent()
    )
    print("👉 Enter your choice [1/2]: ")

    return Scanner(System.`in`).nextLine().trim().ifEmpty { "1" }
}

tasks.register("addStartupWMClassToDebDynamic") {
    group = "release"
    description = "Finds .deb file, modifies .desktop, control files, and DEBIAN scripts, and rebuilds it"

    doLast {
        val debRoot = file("build/compose/binaries")
        if (!debRoot.exists()) throw GradleException("❌ Folder not found: ${debRoot}")

        val allDebs = debRoot.walkTopDown().filter { it.isFile && it.extension == "deb" }.toList()
        if (allDebs.isEmpty()) throw GradleException("❌ No .deb files found under ${debRoot}")

        // picking the latest .deb file
        val originalDeb = allDebs.maxByOrNull { it.lastModified() }!!
        println("📦 Found deb package: ${originalDeb.relativeTo(rootDir)}")

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
        if (!desktopFile.exists()) throw GradleException("❌ .desktop file not found: ${desktopRelativePath}")

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

        for (i in lines.indices) {
            if (lines[i].trim().startsWith("StartupWMClass=")) {
                if (lines[i] != "StartupWMClass=$mainClass") {
                    lines[i] = "StartupWMClass=$mainClass"
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

        println("\n📄 Final .desktop file content:")
        println("--------------------------------")
        desktopFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        // Step 3: Modifying the DEBIAN/control file
        val controlFile = File(workDir, "DEBIAN/control")
        if (!controlFile.exists()) throw GradleException("❌ control file not found: DEBIAN/control")

        val controlLines = controlFile.readLines().toMutableList()

        // Update maintainer field
        var maintainerModified = false
        for (i in controlLines.indices) {
            if (controlLines[i].trim().startsWith("Maintainer:")) {
                controlLines[i] = "Maintainer: $maintainer"
                maintainerModified = true
                println("✅ Modified Maintainer entry")
                break
            }
        }

        // Add maintainer field if it doesn't exist
        if (!maintainerModified) {
            controlLines.add("Maintainer: $maintainer")
            println("✅ Added Maintainer entry")
        }

        // Update description field for better info
        for (i in controlLines.indices) {
            if (controlLines[i].trim().startsWith("Description:")) {
                controlLines[i] = "Description: $controlDescription"
                println("✅ Modified Description entry")
                break
            }
        }

        // Write changes back to control file
        controlFile.writeText(controlLines.joinToString("\n"))

        println("\n📄 Final control file content:")
        println("--------------------------------")
        controlFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        // Step 4: Modifying the DEBIAN/postinst script
        val postinstFile = File(workDir, "DEBIAN/postinst")
        if (!postinstFile.exists()) throw GradleException("❌ postinst file not found: DEBIAN/postinst")

        val postinstContent = """#!/bin/sh
# postinst script for $packageName
#
# see: dh_installdeb(1)

set -e

# summary of how this script can be called:
#        * <postinst> `configure' <most-recently-configured-version>
#        * <old-postinst> `abort-upgrade' <new version>
#        * <conflictor's-postinst> `abort-remove' `in-favour' <package>
#          <new-version>
#        * <postinst> `abort-remove'
#        * <deconfigured's-postinst> `abort-deconfigure' `in-favour'
#          <failed-install-package> <version> `removing'
#          <conflicting-package> <version>
# for details, see https://www.debian.org/doc/debian-policy/ or
# the debian-policy package

case "$1" in
    configure)
        # Install desktop menu entry
        xdg-desktop-menu install /opt/$packageName/lib/$packageName-$packageName.desktop
        
        # Create symlink for terminal access
        if [ ! -L /usr/local/bin/$packageName ]; then
            ln -sf /opt/$packageName/bin/$packageName /usr/local/bin/$packageName
            echo "Created symlink: /usr/local/bin/$packageName -> /opt/$packageName/bin/$packageName"
        fi
    ;;

    abort-upgrade|abort-remove|abort-deconfigure)
    ;;

    *)
        echo "postinst called with unknown argument `$1`" >&2
        exit 1
    ;;
esac

exit 0"""

        postinstFile.writeText(postinstContent)
        println("✅ Updated postinst script to create terminal symlink")

        // Step 5: Modifying the DEBIAN/prerm script
        val prermFile = File(workDir, "DEBIAN/prerm")
        if (!prermFile.exists()) throw GradleException("❌ prerm file not found: DEBIAN/prerm")

        val prermContent = """#!/bin/sh
# prerm script for $packageName
#
# see: dh_installdeb(1)

set -e

# summary of how this script can be called:
#        * <prerm> `remove'
#        * <old-prerm> `upgrade' <new-version>
#        * <new-prerm> `failed-upgrade' <old-version>
#        * <conflictor's-prerm> `remove' `in-favour' <package> <new-version>
#        * <deconfigured's-prerm> `deconfigure' `in-favour'
#          <package-being-installed> <version> `removing'
#          <conflicting-package> <version>
# for details, see https://www.debian.org/doc/debian-policy/ or
# the debian-policy package

case "$1" in
    remove|upgrade|deconfigure)
        # Remove desktop menu entry
        xdg-desktop-menu uninstall /opt/$packageName/lib/$packageName-$packageName.desktop
        
        # Remove terminal symlink
        if [ -L /usr/local/bin/$packageName ]; then
            rm -f /usr/local/bin/$packageName
            echo "Removed symlink: /usr/local/bin/$packageName"
        fi
    ;;

    failed-upgrade)
    ;;

    *)
        echo "prerm called with unknown argument `$1`" >&2
        exit 1
    ;;
esac

exit 0"""

        prermFile.writeText(prermContent)
        println("✅ Updated prerm script to remove terminal symlink")

        // Make sure scripts are executable
        exec {
            commandLine("chmod", "+x", postinstFile.absolutePath)
        }
        exec {
            commandLine("chmod", "+x", prermFile.absolutePath)
        }

        println("\n📄 Final postinst script content:")
        println("--------------------------------")
        postinstFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        println("\n📄 Final prerm script content:")
        println("--------------------------------")
        prermFile.readLines().forEach { println(it) }
        println("--------------------------------\n")

        // Step 6: Repackaging the debian package back
        exec {
            commandLine("dpkg-deb", "-b", workDir.absolutePath, modifiedDeb.absolutePath)
        }

        println("✅ Done: Rebuilt with Name=$appDisplayName, StartupWMClass=$mainClass, updated control file, and terminal symlink -> ${modifiedDeb.name}")
    }
}


tasks.register("packageDebWithWMClass") {
    group = "release"
    description = "Runs packaging task (packageDeb or packageReleaseDeb), then adds StartupWMClass"

    doLast {
        val choice = promptUserChoice()

        val packagingTask = when (choice) {
            "2" -> "packageReleaseDeb"
            else -> "packageDeb"
        }

        println("▶️ Running: ${packagingTask}")
        gradle.includedBuilds.forEach { it.task(":${packagingTask}") } // just in case of composite builds

        exec {
            commandLine("./gradlew clean")
            commandLine("./gradlew", packagingTask)
        }

        tasks.named("addStartupWMClassToDebDynamic").get().actions.forEach { it.execute(this) }
    }
}

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
            set -e
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

        val iconPath = "src/main/composeResources/drawable/composedesktop.png"
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
            ln -sf /opt/$packageName/bin/$packageName /usr/local/bin/$packageName
            gtk-update-icon-cache -q /usr/share/icons/hicolor || true
            update-desktop-database -q /usr/share/applications || true
            exit 0
        """.trimIndent())
        postinstFile.setExecutable(true, false)

        val prermFile = file("${debianRoot.get()}/DEBIAN/prerm")
        prermFile.writeText("""
            #!/bin/sh
            set -e
            rm -f /usr/local/bin/$packageName
            exit 0
        """.trimIndent())
        prermFile.setExecutable(true, false)
    }

    workingDir(debianRoot.get().asFile.parentFile)
    commandLine("dpkg-deb", "--build", "--root-owner-group", debianRoot.get().asFile.name, finalDebDir.get().asFile.path)
}
-dontwarn kotlinx.serialization.**

-dontwarn sun.font.CFont
-dontwarn sun.swing.SwingUtilities2$AATextInfo
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
-keepclasseswithmembers class **$$serializer {
    static **$$serializer INSTANCE;
}


# Keep serializable classes and their properties
-if @kotlinx.serialization.Serializable class **
-keep class <1> {
    static <1>$Companion Companion;
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
-keep class AppSettings$$serializer { *; }


# Apache Commons Compress - suppress warnings for optional dependencies
-dontwarn org.objectweb.asm.**
-dontwarn org.brotli.dec.**
-dontwarn com.github.luben.zstd.**
-dontwarn sun.misc.Cleaner

# Keep Apache Commons Compress classes used in code
-keep class org.apache.commons.compress.archivers.zip.** { *; }
-keep class org.apache.commons.compress.archivers.ArchiveEntry { *; }
-keep class org.apache.commons.compress.archivers.ArchiveOutputStream { *; }

# Suppress notes about dynamic class loading in Apache Commons
-dontnote org.apache.commons.compress.**
-dontnote org.apache.commons.io.**
-dontnote org.apache.commons.lang3.**

# SLF4J
-dontwarn org.slf4j.**
-dontnote org.slf4j.**
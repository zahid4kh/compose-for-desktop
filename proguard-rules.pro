
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

# XZ compression library (not needed for basic ZIP)
-dontwarn org.tukaani.xz.**

# Apache Commons Compress optional formats we don't use
-dontwarn org.apache.commons.compress.archivers.sevenz.**
-dontwarn org.apache.commons.compress.compressors.lzma.**
-dontwarn org.apache.commons.compress.compressors.xz.**
-dontwarn org.apache.commons.compress.harmony.pack200.**
-dontwarn org.apache.commons.compress.harmony.unpack200.**

# Keep Apache Commons Compress classes we actually use
-keep class org.apache.commons.compress.archivers.zip.** { *; }
-keep class org.apache.commons.compress.archivers.ArchiveEntry { *; }
-keep class org.apache.commons.compress.archivers.ArchiveOutputStream { *; }

# Keep Pack200 internal references to avoid the "inconsistent" warnings
-keep class org.apache.commons.compress.harmony.pack200.Pack200ClassReader {
    byte[] b;
}
-keep class org.apache.commons.compress.harmony.pack200.NewAttribute {
    java.lang.String type;
}
-keep class org.apache.commons.compress.harmony.pack200.Segment$SegmentAnnotationVisitor {
    org.objectweb.asm.AnnotationVisitor av;
}

# Suppress notes about dynamic class loading in Apache Commons
-dontnote org.apache.commons.compress.**
-dontnote org.apache.commons.io.**
-dontnote org.apache.commons.lang3.**

# SLF4J
-dontwarn org.slf4j.**
-dontnote org.slf4j.**

# THIS IS A FIX FOR IMAGEIO ICNS SERVICE CONFIGURATION ERROR
-keep class javax.imageio.spi.** { *; }

# Keep TwelveMonkeys ImageIO service providers
-keep class com.twelvemonkeys.imageio.** { *; }

# Keep all service provider implementations
-keep class * implements javax.imageio.spi.ImageReaderSpi { *; }
-keep class * implements javax.imageio.spi.ImageWriterSpi { *; }
-keep class * implements javax.imageio.spi.ImageTranscoderSpi { *; }

-keepdirectories META-INF/services/
-keep class META-INF.services.** { *; }

# Keep service configuration files
-adaptresourcefilenames META-INF/services/**
-adaptresourcefilecontents META-INF/services/**

# Keep ImageIO registry and related classes
-keep class javax.imageio.ImageIO { *; }
-keep class javax.imageio.spi.IIORegistry { *; }
-keep class javax.imageio.spi.ServiceRegistry { *; }

-keep class javax.imageio.ImageReader { *; }
-keep class javax.imageio.ImageWriter { *; }
-keep class javax.imageio.stream.** { *; }

-keep class org.apache.commons.imaging.** { *; }
-dontwarn org.apache.commons.imaging.**
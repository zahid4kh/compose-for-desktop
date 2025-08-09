package projectgen

data class ProjectOptions(
    val appName: String,
    val packageName: String,
    val projectVersion: String,
    val windowWidth: String,
    val windowHeight: String,
    val includeRetrofit: Boolean,
    val includeDeskit: Boolean,
    val includeSQLDelight: Boolean,
    val includeKtor: Boolean,
    val includeDecompose: Boolean,
    val includeImageLoader: Boolean,
    val includePrecompose: Boolean,
    val includeSentry: Boolean,
    val includeMarkdown: Boolean,
    val includeHotReload: Boolean,
    val includeKotlinxDatetime: Boolean,
    val linuxMaintainer: String?,
    val appDescription: String?,
    val attachedPngIcon: String
)
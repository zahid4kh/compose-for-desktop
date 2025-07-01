sealed class ViewIntent {
    data class UpdateAppName(val name: String) : ViewIntent()
    data class UpdatePackageName(val name: String) : ViewIntent()
    data class UpdateVersion(val version: String) : ViewIntent()
    data class ToggleDependency(val dependency: String, val enabled: Boolean) : ViewIntent()
    object ShowPreview : ViewIntent()
    object HidePreview : ViewIntent()
    object GenerateProject : ViewIntent()
    data class SetGenerating(val isGenerating: Boolean) : ViewIntent()
}
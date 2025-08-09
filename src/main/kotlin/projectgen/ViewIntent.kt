package projectgen

import java.io.File

sealed class ViewIntent {
    data class UpdateAppName(val name: String) : ViewIntent()
    data class UpdatePackageName(val name: String) : ViewIntent()
    data class UpdateVersion(val version: String) : ViewIntent()
    data class UpdateWindowWidth(val width: String) : ViewIntent()
    data class UpdateWindowHeight(val height: String) : ViewIntent()
    data class ToggleDependency(val dependency: String, val enabled: Boolean) : ViewIntent()
    data class SetProjectDescription(val description: String): ViewIntent()
    data class SetLinuxMaintainer(val maintainer: String): ViewIntent()
    object ShowPreview : ViewIntent()
    object HidePreview : ViewIntent()
    object GenerateProject : ViewIntent()
    object ShowFileSaver : ViewIntent()
    object HideFileSaver : ViewIntent()
    object HideSuccessDialog : ViewIntent()
    object HideErrorDialog : ViewIntent()
    data class SetSelectedIcon(val icon: File) : ViewIntent()
    data class SaveProjectToFile(val file: File) : ViewIntent()
    data class SetGenerating(val isGenerating: Boolean) : ViewIntent()
}
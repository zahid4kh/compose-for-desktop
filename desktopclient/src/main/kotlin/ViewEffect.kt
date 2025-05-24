sealed class ViewEffect {
    data class ShowError(val message: String) : ViewEffect()
    data class ProjectGenerated(val filePath: String) : ViewEffect()
}
async function addKotlinFiles(folder) {
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

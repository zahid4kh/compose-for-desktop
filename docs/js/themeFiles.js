async function addThemeFiles(folder) {
  try {
    // Color.kt
    const colorContent = await fetchTextFile("src/main/kotlin/theme/Color.kt");
    folder.file("Color.kt", colorContent);

    // Theme.kt
    const themeContent = await fetchTextFile("src/main/kotlin/theme/Theme.kt");
    folder.file("Theme.kt", themeContent);

    // Type.kt
    const typeContent = await fetchTextFile("src/main/kotlin/theme/Type.kt");
    folder.file("Type.kt", typeContent);

    return true;
  } catch (error) {
    console.error("Error adding theme files:", error);
    throw error;
  }
}

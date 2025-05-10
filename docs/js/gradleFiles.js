async function addGradleWrapperFiles(folder) {
  try {
    // gradle-wrapper.properties
    const propertiesContent = await fetchTextFile(
      "gradle/wrapper/gradle-wrapper.properties"
    );
    folder.file("gradle-wrapper.properties", propertiesContent);

    // gradle-wrapper.jar (binary file)
    const jarContent = await fetchBinaryFile(
      "gradle/wrapper/gradle-wrapper.jar"
    );
    folder.file("gradle-wrapper.jar", jarContent);

    return true;
  } catch (error) {
    console.error("Error adding Gradle wrapper files:", error);
    throw error;
  }
}

async function addGradleWrapperScripts(folder) {
  try {
    // gradlew (shell script)
    const gradlewContent = await fetchTextFile("gradlew");
    folder.file("gradlew", gradlewContent);

    // gradlew.bat (batch script)
    const gradlewBatContent = await fetchTextFile("gradlew.bat");
    folder.file("gradlew.bat", gradlewBatContent);

    return true;
  } catch (error) {
    console.error("Error adding Gradle wrapper scripts:", error);
    throw error;
  }
}

// gradle.properties
async function addGradleProperties(folder) {
  try {
    const content = await fetchTextFile("gradle.properties");
    folder.file("gradle.properties", content);
    return true;
  } catch (error) {
    console.error("Error adding gradle.properties:", error);
    throw error;
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("projectForm");
  const generateBtn = document.getElementById("generateBtn");
  const generatingOverlay = document.getElementById("generatingOverlay");

  function updatePreview() {
    const options = {
      appName:
        document.getElementById("appName").value.trim() || "MyComposeApp",
      packageName:
        document.getElementById("packageName").value.trim() ||
        "com.example.myapp",
      projectVersion:
        document.getElementById("projectVersion").value.trim() || "1.0.0",
      windowWidth: document.getElementById("windowWidth").value || "800",
      windowHeight: document.getElementById("windowHeight").value || "600",
      includeRetrofit: document.getElementById("retrofit").checked,
      includeSQLDelight: document.getElementById("sqldelight").checked,
      includeKtor: document.getElementById("ktor").checked,
      includeDecompose: document.getElementById("decompose").checked,
      includeImageLoader: document.getElementById("imageLoader").checked,
      includePrecompose: document.getElementById("precompose").checked,
    };

    const previewContent = generateBuildGradlePreview(options);
    const previewElement = document.getElementById("gradlePreview");
    previewElement.textContent = previewContent;
    Prism.highlightElement(previewElement);
  }

  const inputs = form.querySelectorAll("input");
  inputs.forEach((input) => {
    if (input.type === "checkbox") {
      input.addEventListener("change", updatePreview);
    } else {
      input.addEventListener("input", updatePreview);
    }
  });

  updatePreview();

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    generateProject();
  });

  async function generateProject() {
    generatingOverlay.classList.remove("hidden");

    try {
      const appName = document.getElementById("appName").value.trim();
      const packageName = document.getElementById("packageName").value.trim();
      const projectVersion = document
        .getElementById("projectVersion")
        .value.trim();
      const windowWidth = document.getElementById("windowWidth").value;
      const windowHeight = document.getElementById("windowHeight").value;

      const includeRetrofit = document.getElementById("retrofit").checked;
      const includeSQLDelight = document.getElementById("sqldelight").checked;
      const includeKtor = document.getElementById("ktor").checked;
      const includeDecompose = document.getElementById("decompose").checked;
      const includeImageLoader = document.getElementById("imageLoader").checked;

      if (!appName || !packageName) {
        generatingOverlay.classList.add("hidden");
        showError("Please fill out all required fields");
        return;
      }

      const zip = new JSZip();

      const rootFolder = zip.folder(appName.toLowerCase().replace(/\s+/g, "-"));

      const gradleFolder = rootFolder.folder("gradle");
      const wrapperFolder = gradleFolder.folder("wrapper");

      const srcFolder = rootFolder.folder("src");
      const mainFolder = srcFolder.folder("main");
      const kotlinFolder = mainFolder.folder("kotlin");
      mainFolder.folder("resources");

      const testFolder = srcFolder.folder("test");
      testFolder.folder("kotlin");
      testFolder.folder("resources");

      const themeFolder = kotlinFolder.folder("theme");

      rootFolder.file(".gitignore", await fetchTextFile(".gitignore"));

      await addBuildGradle(rootFolder, {
        appName,
        packageName,
        projectVersion,
        windowWidth,
        windowHeight,
        includeRetrofit,
        includeSQLDelight,
        includeKtor,
        includeDecompose,
        includeImageLoader,
      });

      await addSettingsGradle(rootFolder, { appName });
      await addGradleProperties(rootFolder);
      await addGradleWrapperFiles(wrapperFolder);
      await addGradleWrapperScripts(rootFolder);

      await addMainFile(kotlinFolder, { appName, windowWidth, windowHeight });
      await addAppFile(kotlinFolder);
      await addAppModuleFile(kotlinFolder);
      await addDatabaseFile(kotlinFolder, { appName });
      await addMainViewModelFile(kotlinFolder);
      await addModelsFile(kotlinFolder);

      await addThemeFiles(themeFolder);

      await addReadmeFile(rootFolder, { appName });

      const content = await zip.generateAsync({ type: "blob" });
      saveAs(content, `${appName.toLowerCase().replace(/\s+/g, "-")}.zip`);

      generatingOverlay.classList.add("hidden");
    } catch (error) {
      console.error("Error generating project:", error);
      showError(
        "An error occurred while generating the project. Please try again."
      );
      generatingOverlay.classList.add("hidden");
    } finally {
      generatingOverlay.classList.add("hidden");
    }
  }
});

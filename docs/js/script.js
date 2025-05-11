document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("projectForm");
  const generateBtn = document.getElementById("generateBtn");
  const generatingOverlay = document.getElementById("generatingOverlay");

  const tabButtons = document.querySelectorAll(".tab-button");
  const tabContents = document.querySelectorAll(".tab-content");

  tabButtons.forEach((button) => {
    button.addEventListener("click", () => {
      tabButtons.forEach((btn) => btn.classList.remove("active"));
      tabContents.forEach((content) => content.classList.remove("active"));

      button.classList.add("active");

      const tabId = button.getAttribute("data-tab");
      document.getElementById(`${tabId}-tab`).classList.add("active");
    });
  });

  function updatePreviews() {
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
      includeSentry: document.getElementById("sentry").checked,
      includeMarkdown: document.getElementById("markdown").checked,
      includeHotReload: document.getElementById("hot-reload").checked,
    };

    const gradlePreviewContent = generateBuildGradlePreview(options);
    const gradlePreviewElement = document.getElementById("gradlePreview");
    gradlePreviewElement.textContent = gradlePreviewContent;
    Prism.highlightElement(gradlePreviewElement);

    const settingsPreviewContent = generateSettingsGradlePreview(options);
    const settingsPreviewElement = document.getElementById("settingsPreview");
    settingsPreviewElement.textContent = settingsPreviewContent;
    Prism.highlightElement(settingsPreviewElement);

    const mainPreviewContent = generateMainFilePreview(options);
    const mainPreviewElement = document.getElementById("mainPreview");
    mainPreviewElement.textContent = mainPreviewContent;
    Prism.highlightElement(mainPreviewElement);
  }

  const inputs = form.querySelectorAll("input");
  inputs.forEach((input) => {
    if (input.type === "checkbox") {
      input.addEventListener("change", updatePreviews);
    } else {
      input.addEventListener("input", updatePreviews);
    }
  });

  updatePreviews();

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
      const includePrecompose = document.getElementById("precompose").checked;
      const includeSentry = document.getElementById("sentry");
      const includeMarkdown = document.getElementById("markdown").checked;
      const includeHotReload = document.getElementById("hot-reload").checked;

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
        includePrecompose,
        includeSentry,
        includeMarkdown,
        includeHotReload,
      });

      await addSettingsGradle(rootFolder, { appName });
      await addGradleProperties(rootFolder);
      await addGradleWrapperFiles(wrapperFolder);
      await addGradleWrapperScripts(rootFolder);

      await addMainFile(kotlinFolder, { appName, windowWidth, windowHeight });
      await addDatabaseFile(kotlinFolder, { appName });
      await addKotlinFiles(kotlinFolder);
      await addThemeFiles(themeFolder);
      await addReadmeFile(rootFolder, { appName });
      await addGitignoreFile(rootFolder);

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

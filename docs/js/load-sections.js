const DEBUG = true;

function log(...args) {
  if (DEBUG) {
    console.log(...args);
  }
}

async function loadSection(sectionId, filePath) {
  try {
    log(`Loading section ${sectionId} from ${filePath}`);
    const response = await fetch(filePath);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const html = await response.text();
    const element = document.getElementById(sectionId);
    if (!element) {
      throw new Error(`Element with ID ${sectionId} not found!`);
    }
    element.innerHTML = html;
    console.log(`Section ${sectionId} loaded successfully`);
  } catch (error) {
    console.error(`Error loading section ${sectionId}:`, error);
    const element = document.getElementById(sectionId);
    if (element) {
      element.innerHTML = `<p>Error loading section: ${sectionId}</p>`;
    }
  }
}

async function loadAllSections() {
  const sections = [
    { id: "header-section", file: "sections/header.html" },
    { id: "project-info-section", file: "sections/project-info.html" },
    { id: "ui-config-section", file: "sections/ui-config.html" },
    { id: "dependencies-section", file: "sections/dependencies.html" },
    { id: "preview-section", file: "sections/preview.html" },
    { id: "warning-section-content", file: "sections/warning-section.html" },
    { id: "footer-section", file: "sections/footer.html" },
  ];

  try {
    await Promise.all(
      sections.map((section) => loadSection(section.id, section.file))
    );

    initializeTheme();

    initializeApp();
  } catch (error) {
    console.error("Error loading sections:", error);
  }
}

document.addEventListener("DOMContentLoaded", loadAllSections);

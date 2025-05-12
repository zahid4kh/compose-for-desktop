async function addIconFiles(folder) {
  try {
    // compose.png
    const pngContent = await fetchBinaryFile("icons/compose.png");
    folder.file("compose.png", pngContent);

    // compose.ico
    const icoContent = await fetchBinaryFile("icons/compose.ico");
    folder.file("compose.ico", icoContent);

    // compose.icns
    const icnsContent = await fetchBinaryFile("icons/compose.icns");
    folder.file("compose.icns", icnsContent);

    return true;
  } catch (error) {
    console.error("Error adding icon files:", error);
    throw error;
  }
}

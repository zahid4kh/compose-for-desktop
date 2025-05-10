async function fetchTextFile(path) {
  try {
    const response = await fetch(
      `https://raw.githubusercontent.com/zahid4kh/compose-for-desktop/main/${path}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch ${path}: ${response.status}`);
    }
    return await response.text();
  } catch (error) {
    console.error(`Error fetching ${path}:`, error);
    throw error;
  }
}

async function fetchBinaryFile(path) {
  try {
    const response = await fetch(
      `https://raw.githubusercontent.com/zahid4kh/compose-for-desktop/main/${path}`
    );
    if (!response.ok) {
      throw new Error(`Failed to fetch ${path}: ${response.status}`);
    }
    return await response.arrayBuffer();
  } catch (error) {
    console.error(`Error fetching binary file ${path}:`, error);
    throw error;
  }
}

function showError(message) {
  alert(message);
}

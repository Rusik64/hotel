const API_BASE_URL = "/api";

async function apiRequest(url, options = {}) {
    const token = getToken();

    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`/api${url}`, {
        ...options,
        headers
    });

    if (response.status === 401 || response.status === 403) {
        removeToken();
        removeCurrentUser();
        updateUI();
        return null;
    }

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Ошибка: ${response.status}`);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function showPageError(message) {
    const mainContent = document.getElementById("main-content");
    mainContent.innerHTML = `
        <div class="error-message">
            <h2>Ошибка</h2>
            <p>${escapeHtml(message)}</p>
        </div>
    `;
}

function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}
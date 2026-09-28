const TOKEN_KEY = "auth_token";
const USER_KEY = "current_user";

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
}

function getCurrentUser() {
    const user = localStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
}

function setCurrentUser(user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
}

function removeCurrentUser() {
    localStorage.removeItem(USER_KEY);
}

function isTokenExpired(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.exp * 1000 < Date.now();
    } catch (e) {
        return true;
    }
}

function isAuthenticated() {
    const token = getToken();
    if (!token) return false;

    if (isTokenExpired(token)) {
        removeToken();
        removeCurrentUser();
        return false;
    }

    return true;
}

function openLoginModal() {
    openModal(`
        <div class="modal-header">
            <h2>Вход в систему</h2>
        </div>
        <form id="login-form" class="login-form">
            <div class="form-group">
                <label>Логин</label>
                <input
                    type="text"
                    name="username"
                    required
                    autocomplete="username"
                    autofocus
                >
            </div>

            <div class="form-group">
                <label>Пароль</label>
                <input
                    type="password"
                    name="password"
                    required
                    autocomplete="current-password"
                >
            </div>

            <div class="login-error" id="login-error" style="display: none;"></div>

            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="closeModal()">
                    Отмена
                </button>
                <button type="submit" class="btn btn-primary">
                    Войти
                </button>
            </div>
        </form>
    `);

    document.getElementById("login-form").addEventListener("submit", handleLogin);
}

function showLoginError(message) {
    const error = document.getElementById("login-error");
    if (error) {
        error.textContent = message;
        error.style.display = "block";
    }
}

function hideLoginError() {
    const error = document.getElementById("login-error");
    if (error) {
        error.style.display = "none";
    }
}

async function handleLogin(event) {
    event.preventDefault();

    const form = event.target;
    hideLoginError();

    const username = form.username.value.trim();
    const password = form.password.value;

    if (!username || !password) {
        showLoginError("Заполните все поля");
        return;
    }

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || "Неверный логин или пароль");
        }

        const data = await response.json();

        console.log("Токен получен:", data.token);
        console.log("Пользователь:", data.user);

        setToken(data.token);
        setCurrentUser(data.user);

        closeModal();
        updateUI();

        // Перезагружаем дашборд после входа
        if (typeof loadDashboard === "function") {
            loadDashboard();
        }

    } catch (error) {
        showLoginError(error.message || "Ошибка при входе в систему");
    }
}

function logout() {
    if (!confirm("Выйти из системы?")) {
        return;
    }

    removeToken();
    removeCurrentUser();
    updateUI();

    if (typeof loadDashboard === "function") {
        loadDashboard();
    }
}

function updateUI() {
    const guestActions = document.getElementById("guest-actions");
    const userActions = document.getElementById("user-actions");

    if (!guestActions || !userActions) {
        console.error("Элементы guest-actions или user-actions не найдены");
        return;
    }

    if (isAuthenticated()) {
            guestActions.style.display = "none";
            userActions.style.display = "block";
            updateUserInfo();
            updateMenuVisibility();
        } else {
            guestActions.style.display = "block";
            userActions.style.display = "none";
            updateMenuVisibility();
        }
}

function updateMenuVisibility() {
    const user = getCurrentUser();
    const role = user ? user.role : null;
    const navLinks = document.querySelectorAll('.nav-link');

    navLinks.forEach(link => {
        const allowedRoles = link.dataset.roles;

        // Если атрибута нет (например, у "Главной"), показываем всегда
        if (!allowedRoles) {
            link.style.display = 'flex';
            return;
        }

        const rolesList = allowedRoles.split(',');

        if (role && rolesList.includes(role)) {
            link.style.display = 'flex';
        } else {
            link.style.display = 'none';
        }
    });

    // Если текущая активная страница стала скрытой, переключаем на Главную
    const activeLink = document.querySelector('.nav-link.active');
    if (activeLink && activeLink.style.display === 'none') {
        document.querySelector('.nav-link[data-page="dashboard"]').click();
    }
}

function updateUserInfo() {
    const user = getCurrentUser();
    const userInfo = document.getElementById("user-info");

    if (user && userInfo) {
        const roles = {
            "ADMIN": "Администратор",
            "MANAGER": "Менеджер",
            "RECEPTIONIST": "Администратор ресепшн"
        };

        userInfo.innerHTML = `
            <div class="user-name">${user.lastName} ${user.firstName}</div>
            <div class="user-role">${roles[user.role] || user.role}</div>
        `;
    }
}

function checkAuth() {
    console.log("Проверка авторизации...");
    console.log("Токен:", getToken());
    console.log("Пользователь:", getCurrentUser());

    updateUI();
}
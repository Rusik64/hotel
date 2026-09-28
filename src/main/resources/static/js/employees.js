let employees = [];

async function loadEmployees() {
    try {
        employees = await apiRequest("/employees");
        renderEmployees();
    } catch (error) {
        showPageError(error.message);
    }
}

function renderEmployees() {
    renderCrudPage({
        title: "Сотрудники",
        description: "Управление учетными записями сотрудников",
        createText: "Добавить сотрудника",
        data: employees,
        columns: [
            {
                label: "ID",
                field: "id"
            },
            {
                label: "Логин",
                field: "username"
            },
            {
                label: "ФИО",
                render: item =>
                    `${item.lastName || ""} ${item.firstName || ""} ${item.middleName || ""}`.trim() || "-"
            },
            {
                label: "Роль",
                render: item => {
                    const roles = {
                        "ADMIN": "Администратор",
                        "MANAGER": "Менеджер",
                        "RECEPTIONIST": "Администратор ресепшн"
                    };
                    return roles[item.role] || item.role;
                }
            },
            {
                label: "Статус",
                render: item =>
                    item.active
                        ? `<span class="badge badge-success">Активен</span>`
                        : `<span class="badge badge-danger">Заблокирован</span>`
            }
        ],
        onCreate: openCreateEmployeeModal,
        onEdit: editEmployee,
        onDelete: deleteEmployee
    });
}

function employeeForm(employee = {}) {
    return `
        <div class="form-row">
            <div class="form-group">
                <label>Логин</label>
                <input
                    type="text"
                    name="username"
                    value="${escapeHtml(employee.username || "")}"
                    required
                >
            </div>

            <div class="form-group">
                <label>Пароль ${employee.id ? "(оставьте пустым, чтобы не менять)" : ""}</label>
                <input
                    type="password"
                    name="password"
                    ${employee.id ? "" : "required"}
                    minlength="6"
                >
            </div>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label>Фамилия</label>
                <input
                    type="text"
                    name="lastName"
                    value="${escapeHtml(employee.lastName || "")}"
                    required
                >
            </div>

            <div class="form-group">
                <label>Имя</label>
                <input
                    type="text"
                    name="firstName"
                    value="${escapeHtml(employee.firstName || "")}"
                    required
                >
            </div>
        </div>

        <div class="form-group">
            <label>Отчество</label>
            <input
                type="text"
                name="middleName"
                value="${escapeHtml(employee.middleName || "")}"
            >
        </div>

        <div class="form-row">
            <div class="form-group">
                <label>Роль</label>
                <select name="role" required>
                    <option value="">Выберите роль</option>
                    <option value="ADMIN" ${employee.role === "ADMIN" ? "selected" : ""}>
                        Администратор
                    </option>
                    <option value="MANAGER" ${employee.role === "MANAGER" ? "selected" : ""}>
                        Менеджер
                    </option>
                    <option value="RECEPTIONIST" ${employee.role === "RECEPTIONIST" ? "selected" : ""}>
                        Администратор ресепшн
                    </option>
                </select>
            </div>

            <div class="form-group">
                <label>Статус</label>
                <select name="active">
                    <option value="true" ${employee.active !== false ? "selected" : ""}>
                        Активен
                    </option>
                    <option value="false" ${employee.active === false ? "selected" : ""}>
                        Заблокирован
                    </option>
                </select>
            </div>
        </div>
    `;
}

function openCreateEmployeeModal() {
    openModal(`
        <div class="modal-header">
            <h2>Новый сотрудник</h2>
        </div>
        <form id="employee-form">
            ${employeeForm()}
            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="closeModal()">
                    Отмена
                </button>
                <button type="submit" class="btn btn-primary">
                    Сохранить
                </button>
            </div>
        </form>
    `);

    document.getElementById("employee-form").addEventListener("submit", createEmployee);
}

async function createEmployee(event) {
    event.preventDefault();
    const form = event.target;

    if (!validateForm(form)) {
        return;
    }

    const formData = new FormData(form);
    const employee = {
        username: formData.get("username"),
        password: formData.get("password"),
        firstName: formData.get("firstName"),
        lastName: formData.get("lastName"),
        middleName: formData.get("middleName") || null,
        role: formData.get("role"),
        active: formData.get("active") === "true"
    };

    try {
        await apiRequest("/employees", {
            method: "POST",
            body: JSON.stringify(employee)
        });
        closeModal();
        await loadEmployees();
    } catch (error) {
        alert(error.message);
    }
}

async function editEmployee(id) {
    const employee = employees.find(item => item.id === id);
    if (!employee) return;

    openModal(`
        <div class="modal-header">
            <h2>Редактирование сотрудника</h2>
        </div>
        <form id="employee-form">
            ${employeeForm(employee)}
            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="closeModal()">
                    Отмена
                </button>
                <button type="submit" class="btn btn-primary">
                    Сохранить
                </button>
            </div>
        </form>
    `);

    document.getElementById("employee-form").addEventListener("submit", async event => {
        event.preventDefault();
        const form = event.target;

        if (!validateForm(form)) {
            return;
        }

        const formData = new FormData(form);
        const updatedEmployee = {
            username: formData.get("username"),
            password: formData.get("password") || null,
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            middleName: formData.get("middleName") || null,
            role: formData.get("role"),
            active: formData.get("active") === "true"
        };

        try {
            await apiRequest(`/employees/${id}`, {
                method: "PUT",
                body: JSON.stringify(updatedEmployee)
            });
            closeModal();
            await loadEmployees();
        } catch (error) {
            alert(error.message);
        }
    });
}

async function deleteEmployee(id) {
    if (!confirm("Удалить сотрудника?")) return;

    try {
        await apiRequest(`/employees/${id}`, {
            method: "DELETE"
        });
        await loadEmployees();
    } catch (error) {
        alert(error.message);
    }
}
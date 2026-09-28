let clients = [];


async function loadClients() {

    try {

        clients = await apiRequest("/clients");

        renderClients();

    } catch (error) {

        showPageError(error.message);
    }
}


function renderClients() {

    renderCrudPage({

        title: "Клиенты",

        description:
            "Управление клиентами гостиницы",

        createText:
            "Добавить клиента",

        data: clients,

        columns: [

            {
                label: "ID",
                field: "id"
            },

            {
                label: "Фамилия",
                field: "lastName"
            },

            {
                label: "Имя",
                field: "firstName"
            },

            {
                label: "Отчество",
                field: "middleName"
            },

            {
                label: "Документ",
                field: "identityDocument"
            },

            {
                label: "Серия",
                field: "passportSeries"
            },

            {
                label: "Номер паспорта",
                field: "passportNumber"
            },

            {
                label: "Дата рождения",
                field: "birthDate"
            },

            {
                label: "Пол",
                field: "gender"
            },

            {
                label: "Адрес",
                field: "homeAddress"
            },

            {
                label: "Телефон",
                field: "phone"
            }

        ],

        onCreate: openCreateClientModal,

        onEdit: editClient,

        onDelete: deleteClient
    });
}


function clientForm(client = {}) {

    return `

        <div class="form-row">

            <div class="form-group">
                <label>Фамилия</label>

                <input
                    type="text"
                    name="lastName"
                    value="${escapeHtml(client.lastName || "")}"
                    required
                >
            </div>


            <div class="form-group">
                <label>Имя</label>

                <input
                    type="text"
                    name="firstName"
                    value="${escapeHtml(client.firstName || "")}"
                    required
                >
            </div>

        </div>


        <div class="form-group">

            <label>Отчество</label>

            <input
                type="text"
                name="middleName"
                value="${escapeHtml(client.middleName || "")}"
            >

        </div>


        <div class="form-group">

            <label>Документ</label>

            <input
                type="text"
                name="identityDocument"
                value="${escapeHtml(client.identityDocument || "")}"
            >

        </div>


        <div class="form-row">

                    <div class="form-group">

                        <label>Серия паспорта</label>

                        <input
                            type="text"
                            name="passportSeries"
                            pattern="\\d+"
                            title="Серия паспорта должна содержать только цифры"
                            value="${escapeHtml(client.passportSeries || "")}"
                        >

                    </div>


                    <div class="form-group">

                        <label>Номер паспорта</label>

                        <input
                            type="text"
                            name="passportNumber"
                            pattern="\\d+"
                            title="Номер паспорта должен содержать только цифры"
                            value="${escapeHtml(client.passportNumber || "")}"
                        >

                    </div>

                </div>


        <div class="form-row">

                    <div class="form-group">

                        <label>Дата рождения</label>

                        <input
                            type="date"
                            name="birthDate"
                            max="${new Date().toISOString().split('T')[0]}"
                            value="${client.birthDate || ""}"
                        >

                    </div>


            <div class="form-group">
                <label>Пол</label>
                <select name="gender">
                <option value="">
                   Не указан
                </option>
                <option value="Мужской"
                   ${client.gender === "Мужской" ? "selected" : ""}>
                   Мужской
                </option>
                <option value="Женский"
                    ${client.gender === "Женский" ? "selected" : ""}>
                    Женский
                </option>
                </select>
            </div>
        </div>


        <div class="form-group">

            <label>Домашний адрес</label>

            <input
                type="text"
                name="homeAddress"
                value="${escapeHtml(client.homeAddress || "")}"
            >

        </div>


        <div class="form-group">

            <label>Телефон</label>

            <input
                type="text"
                name="phone"
                pattern="\\+?[0-9()\\-\\s]+"
                value="${escapeHtml(client.phone || "")}"
            >

        </div>
    `;
}


function openCreateClientModal() {

    openModal(`

        <div class="modal-header">
            <h2>Новый клиент</h2>
        </div>

        <form id="client-form">

            ${clientForm()}

            <div class="form-actions">

                <button
                    type="button"
                    class="btn btn-secondary"
                    onclick="closeModal()"
                >
                    Отмена
                </button>

                <button
                    type="submit"
                    class="btn btn-primary"
                >
                    Сохранить
                </button>

            </div>

        </form>
    `);


    document
        .getElementById("client-form")
        .addEventListener("submit", createClient);
}


async function createClient(event) {
    event.preventDefault();

    const form = event.target;

    // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
    if (!validateForm(form)) {
        return;
    }

    const formData = new FormData(form);

    const client = {
        lastName: formData.get("lastName"),
        firstName: formData.get("firstName"),
        middleName: formData.get("middleName") || null,
        identityDocument: formData.get("identityDocument") || null,
        passportSeries: formData.get("passportSeries") || null,
        passportNumber: formData.get("passportNumber") || null,
        birthDate: formData.get("birthDate") || null,
        gender: formData.get("gender") || null,
        homeAddress: formData.get("homeAddress") || null,
        phone: formData.get("phone") || null
    };

    try {
        await apiRequest("/clients", {
            method: "POST",
            body: JSON.stringify(client)
        });

        closeModal();
        await loadClients();

    } catch (error) {
        alert(error.message);
    }
}


async function editClient(id) {

    const client =
        clients.find(item => item.id === id);

    if (!client) {
        return;
    }


    openModal(`

        <div class="modal-header">
            <h2>Редактирование клиента</h2>
        </div>

        <form id="client-form">

            ${clientForm(client)}

            <div class="form-actions">

                <button
                    type="button"
                    class="btn btn-secondary"
                    onclick="closeModal()"
                >
                    Отмена
                </button>

                <button
                    type="submit"
                    class="btn btn-primary"
                >
                    Сохранить
                </button>

            </div>

        </form>
    `);


        // ... внутри editClient, после openModal(...)

        document
            .getElementById("client-form")
            .addEventListener("submit", async event => {
                event.preventDefault();

                const form = event.target;

                // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
                if (!validateForm(form)) {
                    return;
                }

                const formData = new FormData(form);

                const updatedClient = {
                    lastName: formData.get("lastName"),
                    firstName: formData.get("firstName"),
                    middleName: formData.get("middleName") || null,
                    identityDocument: formData.get("identityDocument") || null,
                    passportSeries: formData.get("passportSeries") || null,
                    passportNumber: formData.get("passportNumber") || null,
                    birthDate: formData.get("birthDate") || null,
                    gender: formData.get("gender") || null,
                    homeAddress: formData.get("homeAddress") || null,
                    phone: formData.get("phone") || null
                };

                try {
                    await apiRequest(`/clients/${id}`, {
                        method: "PUT",
                        body: JSON.stringify(updatedClient)
                    });

                    closeModal();
                    await loadClients();

                } catch (error) {
                    alert(error.message);
                }
            });
}


async function deleteClient(id) {

    if (!confirm("Удалить клиента?")) {
        return;
    }


    try {

        await apiRequest(`/clients/${id}`, {
            method: "DELETE"
        });

        await loadClients();

    } catch (error) {

        alert(error.message);
    }
}
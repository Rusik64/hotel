let registrations = [];
let registrationClients = [];
let registrationRooms = [];

function checkDateOverlap(roomNumber, arrivalDate, departureDate, excludeId = null) {
    const overlapping = registrations.filter(reg => {
        // Исключаем текущую регистрацию при редактировании
        if (excludeId && reg.id === excludeId) return false;

        // Проверяем только тот же номер
        if (reg.roomNumber !== roomNumber) return false;

        // Если регистрация не имеет даты выезда, считаем её бесконечной
        const regDeparture = reg.departureDate ? new Date(reg.departureDate) : new Date('2099-12-31');
        const newDeparture = departureDate ? new Date(departureDate) : new Date('2099-12-31');
        const newArrival = new Date(arrivalDate);
        const regArrival = new Date(reg.arrivalDate);

        // Проверка пересечения интервалов
        return newArrival <= regDeparture && newDeparture >= regArrival;
    });

    return overlapping.length > 0;
}

async function loadRegistrations() {

    try {

        // Загружаем регистрации, клиентов и номера одновременно
        const [registrationsData, clientsData, roomsData] = await Promise.all([
            apiRequest("/registrations"),
            apiRequest("/clients"),
            apiRequest("/rooms")
        ]);

        registrations = registrationsData;
        registrationClients = clientsData;
        registrationRooms = roomsData;

        renderRegistrations();

    } catch (error) {

        showPageError(error.message);
    }
}


function renderRegistrations() {

    renderCrudPage({

        title: "Регистрации",

        description:
            "Регистрация заезда и выезда клиентов",

        createText:
            "Новая регистрация",

        data: registrations,

        columns: [

            {
                label: "ID",
                field: "id"
            },

            {
                label: "Клиент",
                render: item => {
                    const client = registrationClients.find(c => c.id === item.clientId);
                    return client
                        ? `${client.lastName || ""} ${client.firstName || ""}`.trim()
                        : "-";
                }
            },

            {
                label: "Номер",
                render: item => {
                    const room = registrationRooms.find(r => r.roomNumber === item.roomNumber);
                    return room ? room.roomNumber : "-";
                }
            },

            {
                label: "Дата заезда",
                field: "arrivalDate"
            },

            {
                label: "Дата выезда",
                field: "departureDate"
            }

        ],

        onCreate: openCreateRegistrationModal,

        onEdit: editRegistration,

        onDelete: deleteRegistration
    });
}


async function loadRegistrationReferences() {

    try {

        registrationClients =
            await apiRequest("/clients");

        registrationRooms =
            await apiRequest("/rooms");

    } catch (error) {

        alert(error.message);
    }
}


function registrationForm(registration = {}) {

    const clientId = registration.clientId || "";

    const roomNumber = registration.roomNumber || "";


    return `

        <div class="form-group">

            <label>Клиент</label>

            <select
                name="clientId"
                required
            >

                <option value="">
                    Выберите клиента
                </option>

                ${registrationClients.map(client => `

                    <option
                        value="${client.id}"
                        ${client.id == clientId ? "selected" : ""}
                    >
                        ${escapeHtml(
                            `${client.lastName} ${client.firstName}`
                        )}
                    </option>

                `).join("")}

            </select>

        </div>


        <div class="form-group">

            <label>Номер</label>

            <select
                name="roomNumber"
                required
            >

                <option value="">
                    Выберите номер
                </option>

                ${registrationRooms.map(room => `

                    <option
                        value="${room.roomNumber}"
                        ${room.roomNumber == roomNumber ? "selected" : ""}
                    >
                        ${room.roomNumber} — ${escapeHtml(room.roomType)}
                    </option>

                `).join("")}

            </select>

        </div>


                <div class="form-row">

                    <div class="form-group">

                        <label>Дата заезда</label>

                        <input
                            type="date"
                            name="arrivalDate"
                            value="${registration.arrivalDate || ""}"
                            required
                            onchange="updateDepartureDateMin(this)"
                        >

                    </div>


                    <div class="form-group">

                        <label>Дата выезда</label>

                        <input
                            type="date"
                            name="departureDate"
                            value="${registration.departureDate || ""}"
                            min="${registration.arrivalDate || ""}"
                        >

                    </div>

                </div>
    `;
}


async function openCreateRegistrationModal() {

    await loadRegistrationReferences();


    openModal(`

        <div class="modal-header">
            <h2>Новая регистрация</h2>
        </div>

        <form id="registration-form">

            ${registrationForm()}

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
        .getElementById("registration-form")
        .addEventListener("submit", createRegistration);
}


function getRegistrationData(form) {
    const data = new FormData(form);
    return {
        clientId: Number(data.get("clientId")),
        roomNumber: Number(data.get("roomNumber")),
        arrivalDate: data.get("arrivalDate"),
        departureDate: data.get("departureDate") || null
    };
}


async function createRegistration(event) {
    event.preventDefault();

    const form = event.target;

    if (!validateForm(form)) {
        return;
    }

    const registration = getRegistrationData(form);

    // ✅ Проверяем пересечение периодов на фронтенде
    if (checkDateOverlap(registration.roomNumber, registration.arrivalDate, registration.departureDate)) {
        alert("Номер уже забронирован на этот период. Выберите другой номер или измените даты.");
        return;
    }

    try {
        await apiRequest("/registrations", {
            method: "POST",
            body: JSON.stringify(registration)
        });

        closeModal();
        await loadRegistrations();

    } catch (error) {
        alert(error.message);
    }
}


async function editRegistration(id) {

    const registration =
        registrations.find(
            item => item.id === id
        );

    if (!registration) {
        return;
    }


    await loadRegistrationReferences();


    openModal(`

        <div class="modal-header">
            <h2>Редактирование регистрации</h2>
        </div>

        <form id="registration-form">

            ${registrationForm(registration)}

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

            document.getElementById("registration-form").addEventListener("submit", async event => {
                event.preventDefault();

                const form = event.target;

                if (!validateForm(form)) {
                    return;
                }

                const updated = getRegistrationData(form);

                // ✅ Проверяем пересечение периодов (исключая текущую регистрацию)
                if (checkDateOverlap(updated.roomNumber, updated.arrivalDate, updated.departureDate, id)) {
                    alert("Номер уже забронирован на этот период. Выберите другой номер или измените даты.");
                    return;
                }

                try {
                    await apiRequest(`/registrations/${id}`, {
                        method: "PUT",
                        body: JSON.stringify(updated)
                    });

                    closeModal();
                    await loadRegistrations();

                } catch (error) {
                    alert(error.message);
                }
            });
}


async function deleteRegistration(id) {

    if (!confirm("Удалить регистрацию?")) {
        return;
    }


    try {

        await apiRequest(
            `/registrations/${id}`,
            {
                method: "DELETE"
            }
        );

        await loadRegistrations();

    } catch (error) {

        alert(error.message);
    }
}

function updateDepartureDateMin(arrivalInput) {
    const form = arrivalInput.closest("form");
    const departureInput = form.querySelector('input[name="departureDate"]');
    if (departureInput && arrivalInput.value) {
        departureInput.min = arrivalInput.value;
        if (departureInput.value && departureInput.value < arrivalInput.value) {
            departureInput.value = "";
        }
    }
}
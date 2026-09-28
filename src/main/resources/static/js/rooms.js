let rooms = [];


async function loadRooms() {

    try {

        rooms = await apiRequest("/rooms");

        renderRooms();

    } catch (error) {

        showPageError(error.message);
    }
}


function renderRooms() {

    renderCrudPage({

        title: "Номера",

        description:
            "Управление номерами гостиницы",

        createText:
            "Добавить номер",

        data: rooms,

        getId: item => item.roomNumber,

        columns: [

            {
                label: "Номер",
                field: "roomNumber"
            },

            {
                label: "Тип",
                field: "roomType"
            },

            {
                label: "Занят",
                render: room =>
                    room.occupied
                        ? `<span class="badge badge-danger">Да</span>`
                        : `<span class="badge badge-success">Нет</span>`
            },

            {
                label: "Количество комнат",
                field: "numberOfRooms"
            },

            {
                label: "Этаж",
                field: "floor"
            },

            {
                label: "Телефон",
                field: "phone"
            },

            {
                label: "Цена",
                render: room =>
                    room.roomPrice != null
                        ? `${room.roomPrice} ₽`
                        : "-"
            }

        ],

        onCreate: openCreateRoomModal,

        onEdit: editRoom,

        onDelete: deleteRoom
    });
}


function roomForm(room = {}) {

    return `

        <div class="form-group">

            <label>Тип номера</label>

            <input
                type="text"
                name="roomType"
                value="${escapeHtml(room.roomType || "")}"
                required
            >

        </div>


        <div class="form-row">

            <div class="form-group">

                <label>Занят</label>

                <select name="occupied">

                    <option value="false"
                        ${room.occupied === false ? "selected" : ""}>
                        Нет
                    </option>

                    <option value="true"
                        ${room.occupied === true ? "selected" : ""}>
                        Да
                    </option>

                </select>

            </div>


            <div class="form-group">

                <label>Количество комнат</label>

                <input
                    type="number"
                    name="numberOfRooms"
                    min="0"
                    value="${room.numberOfRooms ?? ""}"
                >

            </div>

        </div>


        <div class="form-row">

            <div class="form-group">

                <label>Этаж</label>

                <input
                    type="number"
                    name="floor"
                    min="0"
                    value="${room.floor ?? ""}"
                >

            </div>


            <div class="form-group">

                <label>Телефон</label>

                <input
                    type="text"
                    name="phone"
                    value="${escapeHtml(room.phone || "")}"
                >

            </div>

        </div>


        <div class="form-group">

            <label>Цена номера</label>

            <input
                type="number"
                name="roomPrice"
                step="0.01"
                min="0.01"
                value="${room.roomPrice ?? ""}"
                required
            >

        </div>
    `;
}


function openCreateRoomModal() {

    openModal(`

        <div class="modal-header">
            <h2>Новый номер</h2>
        </div>

        <form id="room-form">

            ${roomForm()}

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
        .getElementById("room-form")
        .addEventListener("submit", createRoom);
}


function getRoomFormData(form) {

    const data =
        new FormData(form);


    return {

        roomType:
            data.get("roomType"),

        occupied:
            data.get("occupied") === "true",

        numberOfRooms:
            data.get("numberOfRooms")
                ? Number(data.get("numberOfRooms"))
                : null,

        floor:
            data.get("floor")
                ? Number(data.get("floor"))
                : null,

        phone:
            data.get("phone") || null,

        roomPrice:
            Number(data.get("roomPrice"))
    };
}


async function createRoom(event) {
    event.preventDefault();

    const form = event.target;

    // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
    if (!validateForm(form)) {
        return;
    }

    try {
        const room = getRoomFormData(form);

        await apiRequest("/rooms", {
            method: "POST",
            body: JSON.stringify(room)
        });

        closeModal();
        await loadRooms();

    } catch (error) {
        alert(error.message);
    }
}


async function editRoom(id) {

    const room =
        rooms.find(item => item.roomNumber === id);

    if (!room) {
        return;
    }


    openModal(`

        <div class="modal-header">
            <h2>Редактирование номера</h2>
        </div>

        <form id="room-form">

            ${roomForm(room)}

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


        // ... внутри editRoom, после openModal(...)

        document
            .getElementById("room-form")
            .addEventListener("submit", async event => {
                event.preventDefault();

                const form = event.target;

                // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
                if (!validateForm(form)) {
                    return;
                }

                try {
                    const room = getRoomFormData(form);

                    await apiRequest(`/rooms/${id}`, {
                        method: "PUT",
                        body: JSON.stringify(room)
                    });

                    closeModal();
                    await loadRooms();

                } catch (error) {
                    alert(error.message);
                }
            });
}

async function deleteRoom(id) {

    if (!confirm("Удалить номер?")) {
        return;
    }


    try {

        await apiRequest(`/rooms/${id}`, {
            method: "DELETE"
        });

        await loadRooms();

    } catch (error) {

        alert(error.message);
    }
}

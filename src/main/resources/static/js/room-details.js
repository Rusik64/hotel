let roomDetails = [];


async function loadRoomDetails() {

    try {

        roomDetails =
            await apiRequest("/room-details");

        renderRoomDetails();

    } catch (error) {

        showPageError(error.message);
    }
}


function renderRoomDetails() {

    renderCrudPage({

        title: "Детали номеров",

        description:
            "Дополнительная информация о номерах",

        createText:
            "Добавить детали",

        data: roomDetails,

        getId: item => item.roomNumber,

        columns: [

            {
                label: "Номер",
                field: "roomNumber"
            },

            {
                label: "Цена за день",
                render: item =>
                    item.dailyRoomPrice != null
                        ? `${item.dailyRoomPrice} ₽`
                        : "-"
            },

            {
                label: "Информация о бронировании",
                field: "bookingInformation"
            },

            {
                label: "Свободных мест",
                field: "availablePlaces"
            },

            {
                label: "Фактических жильцов",
                field: "actualResidents"
            }

        ],

        onCreate: openCreateRoomDetailsModal,

        onEdit: editRoomDetails,

        onDelete: deleteRoomDetails
    });
}


function roomDetailsForm(item = {}) {

    return `

        <div class="form-group">

            <label>Номер комнаты</label>

            <input
                type="number"
                name="roomNumber"
                value="${item.roomNumber ?? ""}"
                required
            >

        </div>


        <div class="form-group">

            <label>Цена за день</label>

            <input
                type="number"
                name="dailyRoomPrice"
                step="0.01"
                min="0.01"
                value="${item.dailyRoomPrice ?? ""}"
            >

        </div>


        <div class="form-group">

            <label>Информация о бронировании</label>

            <textarea
                name="bookingInformation"
            >${escapeHtml(item.bookingInformation || "")}</textarea>

        </div>


        <div class="form-row">

            <div class="form-group">

                <label>Свободных мест</label>

                <input
                    type="number"
                    name="availablePlaces"
                    min="0"
                    value="${item.availablePlaces ?? ""}"
                >

            </div>


            <div class="form-group">

                <label>Фактических жильцов</label>

                <input
                    type="number"
                    name="actualResidents"
                    min="0"
                    value="${item.actualResidents ?? ""}"
                >

            </div>

        </div>
    `;
}


function openCreateRoomDetailsModal() {

    openModal(`

        <div class="modal-header">
            <h2>Новые детали номера</h2>
        </div>

        <form id="room-details-form">

            ${roomDetailsForm()}

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
        .getElementById("room-details-form")
        .addEventListener("submit", createRoomDetails);
}


function getRoomDetailsFormData(form) {

    const data =
        new FormData(form);


    return {

        roomNumber:
            Number(data.get("roomNumber")),

        dailyRoomPrice:
            data.get("dailyRoomPrice")
                ? Number(data.get("dailyRoomPrice"))
                : null,

        bookingInformation:
            data.get("bookingInformation") || null,

        availablePlaces:
            data.get("availablePlaces")
                ? Number(data.get("availablePlaces"))
                : null,

        actualResidents:
            data.get("actualResidents")
                ? Number(data.get("actualResidents"))
                : null
    };
}


async function createRoomDetails(event) {
    event.preventDefault();

    const form = event.target;

    // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
    if (!validateForm(form)) {
        return;
    }

    try {
        const item = getRoomDetailsFormData(form);
        await apiRequest(`/room-details/room/${item.roomNumber}`, {
            method: "POST",
            body: JSON.stringify(item)
        });
        closeModal();
        await loadRoomDetails();
    } catch (error) {
        alert(error.message);
    }
}


async function editRoomDetails(id) {

    const item =
        roomDetails.find(
            element => element.roomNumber === id
        );

    if (!item) {
        return;
    }


    openModal(`

        <div class="modal-header">
            <h2>Редактирование деталей</h2>
        </div>

        <form id="room-details-form">

            ${roomDetailsForm(item)}

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
            .getElementById("room-details-form")
            .addEventListener("submit", async event => {
                event.preventDefault();

                const form = event.target;

                // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
                if (!validateForm(form)) {
                    return;
                }

                try {
                    const updated = getRoomDetailsFormData(form);

                    await apiRequest(
                        `/room-details/${id}`,
                        {
                            method: "PUT",
                            body: JSON.stringify(updated)
                        }
                    );

                    closeModal();
                    await loadRoomDetails();

                } catch (error) {
                    alert(error.message);
                }
            });
}


async function deleteRoomDetails(id) {

    if (!confirm("Удалить детали номера?")) {
        return;
    }


    try {

        await apiRequest(
            `/room-details/${id}`,
            {
                method: "DELETE"
            }
        );

        await loadRoomDetails();

    } catch (error) {

        alert(error.message);
    }
}
let services = [];


async function loadServices() {

    try {

        services =
            await apiRequest("/services");

        renderServices();

    } catch (error) {

        showPageError(error.message);
    }
}


function renderServices() {

    renderCrudPage({

        title: "Услуги",

        description:
            "Услуги гостиницы",

        createText:
            "Добавить услугу",

        data: services,

        columns: [

            {
                label: "ID",
                field: "id"
            },

            {
                label: "Название",
                field: "serviceName"
            },

            {
                label: "Цена",
                render: item =>
                    item.price != null
                        ? `${item.price} ₽`
                        : "-"
            }

        ],

        onCreate: openCreateServiceModal,

        onEdit: editService,

        onDelete: deleteService
    });
}


function serviceForm(service = {}) {

    return `

        <div class="form-group">

            <label>Название услуги</label>

            <input
                type="text"
                name="serviceName"
                value="${escapeHtml(service.serviceName || "")}"
                required
            >

        </div>


        <div class="form-group">

            <label>Цена</label>

            <input
                type="number"
                name="price"
                min="0.01"
                step="0.01"
                value="${service.price ?? ""}"
                required
            >

        </div>
    `;
}


function openCreateServiceModal() {

    openModal(`

        <div class="modal-header">
            <h2>Новая услуга</h2>
        </div>

        <form id="service-form">

            ${serviceForm()}

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
        .getElementById("service-form")
        .addEventListener("submit", createService);
}


function getServiceData(form) {

    const data =
        new FormData(form);


    return {

        serviceName:
            data.get("serviceName"),

        price:
            Number(data.get("price"))
    };
}


async function createService(event) {
    event.preventDefault();

    const form = event.target;

    // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
    if (!validateForm(form)) {
        return;
    }

    try {
        const service = getServiceData(form);

        await apiRequest("/services", {
            method: "POST",
            body: JSON.stringify(service)
        });

        closeModal();
        await loadServices();

    } catch (error) {
        alert(error.message);
    }
}


async function editService(id) {

    const service =
        services.find(
            item => item.id === id
        );

    if (!service) {
        return;
    }


    openModal(`

        <div class="modal-header">
            <h2>Редактирование услуги</h2>
        </div>

        <form id="service-form">

            ${serviceForm(service)}

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


        // ... внутри editService, после openModal(...)

        document
            .getElementById("service-form")
            .addEventListener("submit", async event => {
                event.preventDefault();

                const form = event.target;

                // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
                if (!validateForm(form)) {
                    return;
                }

                try {
                    const updated = getServiceData(form);

                    await apiRequest(`/services/${id}`, {
                        method: "PUT",
                        body: JSON.stringify(updated)
                    });

                    closeModal();
                    await loadServices();

                } catch (error) {
                    alert(error.message);
                }
            });
}


async function deleteService(id) {

    if (!confirm("Удалить услугу?")) {
        return;
    }


    try {

        await apiRequest(`/services/${id}`, {

            method: "DELETE"

        });

        await loadServices();

    } catch (error) {

        alert(error.message);
    }
}
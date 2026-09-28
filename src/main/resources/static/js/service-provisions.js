let serviceProvisions = [];
let provisionServices = [];

async function loadServiceProvisions() {

    try {

        serviceProvisions = await apiRequest("/service-provisions");
        provisionServices = await apiRequest("/services");
        renderServiceProvisions();

    } catch (error) {

        showPageError(error.message);
    }
}

function renderServiceProvisions() {

    renderCrudPage({

        title: "Оказание услуг",

        description:
            "Учёт оказанных гостиничных услуг",

        createText:
            "Оказать услугу",

        data: serviceProvisions,

        columns: [

            {
                label: "ID",
                field: "id"
            },

            {
                label: "Услуга",
                render: item => {
                    const service = provisionServices.find(s => s.id === item.serviceId);
                    return service ? service.serviceName : "-";
                }
            },

            {
                label: "Дата",
                field: "provisionDate"
            },

            {
                label: "Количество",
                field: "quantity"
            }

        ],

        onCreate: openCreateServiceProvisionModal,

        onEdit: editServiceProvision,

        onDelete: deleteServiceProvision
    });
}


async function loadProvisionServices() {

    provisionServices =
        await apiRequest("/services");
}


function serviceProvisionForm(item = {}) {

    const serviceId = item.serviceId || "";


    return `

        <div class="form-group">

            <label>Услуга</label>

            <select
                name="serviceId"
                required
            >

                <option value="">
                    Выберите услугу
                </option>

                ${provisionServices.map(service => `

                    <option
                        value="${service.id}"
                        ${service.id == serviceId
                            ? "selected"
                            : ""}
                    >
                        ${escapeHtml(service.serviceName)}
                    </option>

                `).join("")}

            </select>

        </div>


        <div class="form-row">

            <div class="form-group">

                <label>Дата оказания</label>

                <input
                    type="date"
                    name="provisionDate"
                    value="${item.provisionDate || ""}"
                    required
                >

            </div>


            <div class="form-group">

                <label>Количество</label>

                <input
                    type="number"
                    name="quantity"
                    min="1"
                    value="${item.quantity ?? 1}"
                    required
                >

            </div>

        </div>
    `;
}


async function openCreateServiceProvisionModal() {

    try {

        await loadProvisionServices();

    } catch (error) {

        alert(error.message);

        return;
    }


    openModal(`

        <div class="modal-header">
            <h2>Оказание услуги</h2>
        </div>

        <form id="service-provision-form">

            ${serviceProvisionForm()}

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
        .getElementById("service-provision-form")
        .addEventListener(
            "submit",
            createServiceProvision
        );
}


function getServiceProvisionData(form) {

    const data = new FormData(form);

    return {

        serviceId:
            Number(data.get("serviceId")),

        provisionDate:
            data.get("provisionDate"),

        quantity:
            Number(data.get("quantity"))
    };
}


async function createServiceProvision(event) {
    event.preventDefault();

    const form = event.target;

    // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
    if (!validateForm(form)) {
        return;
    }

    try {
        const item = getServiceProvisionData(form);

        await apiRequest("/service-provisions", {
            method: "POST",
            body: JSON.stringify(item)
        });

        closeModal();
        await loadServiceProvisions();

    } catch (error) {
        alert(error.message);
    }
}


async function editServiceProvision(id) {

    const item =
        serviceProvisions.find(
            element => element.id === id
        );

    if (!item) {
        return;
    }


    try {

        await loadProvisionServices();

    } catch (error) {

        alert(error.message);

        return;
    }


    openModal(`

        <div class="modal-header">
            <h2>Редактирование оказания услуги</h2>
        </div>

        <form id="service-provision-form">

            ${serviceProvisionForm(item)}

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


        // ... внутри editServiceProvision, после openModal(...)

        document
            .getElementById("service-provision-form")
            .addEventListener("submit", async event => {
                event.preventDefault();

                const form = event.target;

                // ДОБАВЛЯЕМ ВАЛИДАЦИЮ
                if (!validateForm(form)) {
                    return;
                }

                try {
                    const updated = getServiceProvisionData(form);

                    await apiRequest(
                        `/service-provisions/${id}`,
                        {
                            method: "PUT",
                            body: JSON.stringify(updated)
                        }
                    );

                    closeModal();
                    await loadServiceProvisions();

                } catch (error) {
                    alert(error.message);
                }
            });
}


async function deleteServiceProvision(id) {

    if (!confirm("Удалить запись об оказании услуги?")) {
        return;
    }


    try {

        await apiRequest(
            `/service-provisions/${id}`,
            {
                method: "DELETE"
            }
        );

        await loadServiceProvisions();

    } catch (error) {

        alert(error.message);
    }
}
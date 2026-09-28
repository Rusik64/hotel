function showPageError(message) {

    const mainContent =
        document.getElementById("main-content");

    mainContent.innerHTML = `
        <div class="page">

            <div class="page-error">
                ${escapeHtml(message)}
            </div>

        </div>
    `;
}


function escapeHtml(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


function renderCrudPage(config) {

    const mainContent =
        document.getElementById("main-content");

    mainContent.innerHTML = `

        <div class="page">

            <div class="page-header">

                <div>
                    <h1>${escapeHtml(config.title)}</h1>

                    <p>
                        ${escapeHtml(config.description || "")}
                    </p>
                </div>

                <button
                    class="btn btn-primary"
                    id="create-button"
                >
                    + ${escapeHtml(config.createText || "Добавить")}
                </button>

            </div>


            <div class="toolbar">

                <input
                    type="text"
                    id="search-input"
                    class="search-input"
                    placeholder="Поиск..."
                >

            </div>


            <div class="table-container">

                <table>

                    <thead>
                        <tr>

                            ${config.columns.map(column => `
                                <th>
                                    ${escapeHtml(column.label)}
                                </th>
                            `).join("")}

                            <th>Действия</th>

                        </tr>
                    </thead>

                    <tbody id="table-body"></tbody>

                </table>

            </div>

        </div>
    `;


    document
        .getElementById("create-button")
        .addEventListener("click", config.onCreate);


    const searchInput =
        document.getElementById("search-input");


    searchInput.addEventListener("input", event => {

        const search =
            event.target.value
                .trim()
                .toLowerCase();

        const filtered =
            config.data.filter(item => {

                return JSON.stringify(item)
                    .toLowerCase()
                    .includes(search);

            });

        renderTableRows(filtered, config);
    });


    renderTableRows(config.data, config);
}

function renderTableRows(data, config) {

    const tableBody =
        document.getElementById("table-body");


    if (!data || data.length === 0) {

        tableBody.innerHTML = `

            <tr>

                <td
                    colspan="${config.columns.length + 1}"
                    class="empty-table"
                >
                    Данные отсутствуют
                </td>

            </tr>

        `;

        return;
    }


    tableBody.innerHTML =
        data.map(item => {

            const cells =
                config.columns.map(column => {

                    let value;

                    if (column.render) {

                        value = column.render(item);

                    } else {

                        value = item[column.field];

                    }

                    return `
                        <td>
                            ${value ?? "-"}
                        </td>
                    `;

                }).join("");


            /*
             * Получаем идентификатор объекта.
             *
             * Для большинства сущностей это item.id.
             * Для Room и RoomDetails это item.roomNumber.
             *
             * Если в конфигурации страницы указан getId,
             * используем его.
             */
            const itemId =
                config.getId
                    ? config.getId(item)
                    : item.id;


            return `

                <tr>

                    ${cells}

                    <td>

                        <div class="table-actions">

                            <button
                                class="btn btn-secondary btn-small edit-button"
                                data-id="${itemId}"
                            >
                                Изменить
                            </button>

                            <button
                                class="btn btn-danger btn-small delete-button"
                                data-id="${itemId}"
                            >
                                Удалить
                            </button>

                        </div>

                    </td>

                </tr>

            `;

        }).join("");


    tableBody
        .querySelectorAll(".edit-button")
        .forEach(button => {

            button.addEventListener("click", () => {

                const id =
                    Number(button.dataset.id);

                config.onEdit(id);

            });

        });


    tableBody
        .querySelectorAll(".delete-button")
        .forEach(button => {

            button.addEventListener("click", () => {

                const id =
                    Number(button.dataset.id);

                config.onDelete(id);

            });

        });
}

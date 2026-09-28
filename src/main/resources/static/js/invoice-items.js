let invoiceItems = [];
let invoicePaymentCards = [];
let invoiceServiceProvisions = [];
let invoiceServices = [];

async function loadInvoiceItems() {
    try {
        const [itemsData, cardsData, provisionsData, servicesData] = await Promise.all([
            apiRequest("/invoice-items"),
            apiRequest("/payment-cards"),
            apiRequest("/service-provisions"),
            apiRequest("/services")
        ]);

        invoiceItems = itemsData;
        invoicePaymentCards = cardsData;
        invoiceServiceProvisions = provisionsData;
        invoiceServices = servicesData;

        renderInvoiceItems();

    } catch (error) {
        showPageError(error.message);
    }
}

function renderInvoiceItems() {
    renderCrudPage({
        title: "Счета",
        description: "Позиции счетов за оказанные услуги",
        createText: "Добавить позицию",
        data: invoiceItems,
        columns: [
            {
                label: "ID",
                field: "id"
            },
            {
                label: "Платёжная карта",
                render: item => {
                    const card = invoicePaymentCards.find(c => c.id === item.paymentCardId);
                    return card ? `Карта №${card.id}` : "-";
                }
            },
            {
                label: "Оказание услуги",
                render: item => {
                    const provision = invoiceServiceProvisions.find(p => p.id === item.serviceProvisionId);
                    if (!provision) return "-";

                    const service = invoiceServices.find(s => s.id === provision.serviceId);
                    return service ? `${provision.id} — ${escapeHtml(service.serviceName)} (×${provision.quantity})` : `Услуга №${provision.id}`;
                }
            },
            {
                label: "Сумма",
                render: item =>
                    item.amount != null
                        ? `${item.amount} ₽`
                        : "-"
            }
        ],
        onCreate: openCreateInvoiceItemModal,
        onEdit: editInvoiceItem,
        onDelete: deleteInvoiceItem
    });
}

async function loadInvoiceReferences() {
    try {
        const [cardsData, provisionsData, servicesData] = await Promise.all([
            apiRequest("/payment-cards"),
            apiRequest("/service-provisions"),
            apiRequest("/services")
        ]);

        invoicePaymentCards = cardsData;
        invoiceServiceProvisions = provisionsData;
        invoiceServices = servicesData;
    } catch (error) {
        alert(error.message);
    }
}

// ✅ ИСПРАВЛЕНО: Теперь учитывается количество (quantity)
function calculateInvoiceAmount(serviceProvisionId) {
    if (!serviceProvisionId) return 0;

    const provision = invoiceServiceProvisions.find(p => p.id === serviceProvisionId);
    if (!provision || !provision.serviceId) return 0;

    const service = invoiceServices.find(s => s.id === provision.serviceId);
    if (!service || !service.price) return 0;

    const quantity = provision.quantity || 1;
    // Умножаем цену на количество
    return Number((service.price * quantity).toFixed(2));
}

function invoiceItemForm(item = {}) {
    const paymentCardId = item.paymentCardId || "";
    const serviceProvisionId = item.serviceProvisionId || "";

    return `
        <div class="form-group">
            <label>Платёжная карта</label>
            <select name="paymentCardId" required>
                <option value="">Выберите карту</option>
                ${invoicePaymentCards.map(card => `
                    <option value="${card.id}" ${card.id == paymentCardId ? "selected" : ""}>
                        Карта №${card.id}
                    </option>
                `).join("")}
            </select>
        </div>

        <div class="form-group">
            <label>Оказание услуги</label>
            <select name="serviceProvisionId" id="serviceProvisionSelect" required>
                <option value="">Выберите оказание услуги</option>
                ${invoiceServiceProvisions.map(provision => {
                    const service = invoiceServices.find(s => s.id === provision.serviceId);
                    const serviceName = service ? escapeHtml(service.serviceName) : "Услуга";
                    return `
                        <option value="${provision.id}" ${provision.id == serviceProvisionId ? "selected" : ""}>
                            ${provision.id} — ${serviceName} (кол-во: ${provision.quantity})
                        </option>
                    `;
                }).join("")}
            </select>
            <!-- Подсказка с расчетом -->
            <div id="amount-calculation-info" style="color: #64748b; font-size: 13px; margin-top: 6px;">
                Выберите услугу для автоматического расчета суммы
            </div>
        </div>
    `;
}

// Функция для обновления текстовой подсказки о расчете
function updateAmountInfo() {
    const select = document.getElementById("serviceProvisionSelect");
    const infoDiv = document.getElementById("amount-calculation-info");

    if (!select || !infoDiv) return;

    const provisionId = Number(select.value);
    const provision = invoiceServiceProvisions.find(p => p.id === provisionId);

    if (provision) {
        const service = invoiceServices.find(s => s.id === provision.serviceId);
        if (service && service.price) {
            const quantity = provision.quantity || 1;
            const total = (service.price * quantity).toFixed(2);
            infoDiv.innerHTML = `💡 Расчет: ${service.price} ₽ × ${quantity} шт. = <strong>${total} ₽</strong>`;
            return;
        }
    }
    infoDiv.innerHTML = "⚠️ Не удалось рассчитать сумму (проверьте цену услуги)";
}

async function openCreateInvoiceItemModal() {
    try {
        await loadInvoiceReferences();
    } catch (error) {
        alert(error.message);
        return;
    }

    openModal(`
        <div class="modal-header">
            <h2>Новая позиция счёта</h2>
        </div>
        <form id="invoice-item-form">
            ${invoiceItemForm()}
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

    // Добавляем слушатель для динамического обновления подсказки
    document.getElementById("serviceProvisionSelect").addEventListener("change", updateAmountInfo);
    // Инициализируем подсказку сразу (на случай редактирования или если значение уже выбрано)
    setTimeout(updateAmountInfo, 50);

    document.getElementById("invoice-item-form").addEventListener("submit", createInvoiceItem);
}

function getInvoiceItemData(form) {
    const data = new FormData(form);

    const serviceProvisionId = Number(data.get("serviceProvisionId"));

    // Автоматически рассчитываем сумму: цена × количество
    const amount = calculateInvoiceAmount(serviceProvisionId);

    return {
        paymentCardId: Number(data.get("paymentCardId")),
        serviceProvisionId: serviceProvisionId,
        amount: amount
    };
}

async function createInvoiceItem(event) {
    event.preventDefault();
    const form = event.target;

    if (!validateForm(form)) {
        return;
    }

    const item = getInvoiceItemData(form);

    if (item.amount <= 0) {
        alert("Не удалось рассчитать сумму. Проверьте, что у выбранной услуги указана цена и количество.");
        return;
    }

    try {
        await apiRequest("/invoice-items", {
            method: "POST",
            body: JSON.stringify(item)
        });

        closeModal();
        await loadInvoiceItems(); // ✅ Это теперь обновит и платежные карты
    } catch (error) {
        alert(error.message);
    }
}

async function editInvoiceItem(id) {
    const item = invoiceItems.find(element => element.id === id);
    if (!item) return;

    try {
        await loadInvoiceReferences();
    } catch (error) {
        alert(error.message);
        return;
    }

    openModal(`
        <div class="modal-header">
            <h2>Редактирование позиции</h2>
        </div>
        <form id="invoice-item-form">
            ${invoiceItemForm(item)}
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

    document.getElementById("serviceProvisionSelect").addEventListener("change", updateAmountInfo);
    setTimeout(updateAmountInfo, 50);

    document.getElementById("invoice-item-form").addEventListener("submit", async event => {
        event.preventDefault();
        const form = event.target;

        if (!validateForm(form)) {
            return;
        }

        const updated = getInvoiceItemData(form);

        if (updated.amount <= 0) {
            alert("Не удалось рассчитать сумму. Проверьте, что у выбранной услуги указана цена и количество.");
            return;
        }

        try {
            await apiRequest(`/invoice-items/${id}`, {
                method: "PUT",
                body: JSON.stringify(updated)
            });

            closeModal();
            await loadInvoiceItems(); // ✅ Это теперь обновит и платежные карты
        } catch (error) {
            alert(error.message);
        }
    });
}

async function deleteInvoiceItem(id) {
    if (!confirm("Удалить позицию счёта?")) {
        return;
    }

    try {
        await apiRequest(`/invoice-items/${id}`, {
            method: "DELETE"
        });
        await loadInvoiceItems(); // ✅ Это теперь обновит и платежные карты
    } catch (error) {
        alert(error.message);
    }
}
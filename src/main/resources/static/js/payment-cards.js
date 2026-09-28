let paymentCards = [];
let paymentCardRegistrations = [];
let paymentCardRooms = [];
let paymentCardInvoiceItems = [];

async function loadPaymentCards() {
    try {
        const [cardsData, registrationsData, roomsData, invoiceItemsData] = await Promise.all([
            apiRequest("/payment-cards"),
            apiRequest("/registrations"),
            apiRequest("/rooms"),
            apiRequest("/invoice-items")
        ]);

        paymentCards = cardsData;
        paymentCardRegistrations = registrationsData;
        paymentCardRooms = roomsData;
        paymentCardInvoiceItems = invoiceItemsData;

        renderPaymentCards();
    } catch (error) {
        showPageError(error.message);
    }
}

// ✅ ИСПРАВЛЕННАЯ ФУНКЦИЯ РАСЧЕТА
function getCalculatedCardAmount(card) {
    // 1. Считаем базовую стоимость проживания (Дни × Цена номера)
    const registration = paymentCardRegistrations.find(r => r.id === card.registrationId);
    let baseAmount = 0;

    if (registration) {
        const room = paymentCardRooms.find(r => r.roomNumber === registration.roomNumber);
        const dailyPrice = room ? room.roomPrice : 0;
        const paidDays = card.paidDays || 0;
        baseAmount = paidDays * dailyPrice;
    }

    // 2. Считаем сумму всех привязанных счетов (дополнительных услуг)
    // Используем Number() для надежного сравнения ID
    const serviceTotal = paymentCardInvoiceItems
        .filter(item => Number(item.paymentCardId) === Number(card.id))
        .reduce((sum, item) => sum + (Number(item.amount) || 0), 0);

    // 3. Итоговая сумма = Базовая стоимость + Услуги
    const total = baseAmount + serviceTotal;
    return Number(total.toFixed(2));
}

function renderPaymentCards() {
    renderCrudPage({
        title: "Платёжные карты",
        description: "Расчёты по регистрациям",
        createText: "Добавить карту",
        data: paymentCards,
        columns: [
            {
                label: "ID",
                field: "id"
            },
            {
                label: "Регистрация",
                render: item => {
                    const registration = paymentCardRegistrations.find(r => r.id === item.registrationId);
                    return registration ? `№${registration.id}` : "-";
                }
            },
            {
                label: "Тип номера",
                render: item => {
                    const registration = paymentCardRegistrations.find(r => r.id === item.registrationId);
                    if (!registration) return "-";

                    const room = paymentCardRooms.find(r => r.roomNumber === registration.roomNumber);
                    return room ? room.roomType : "-";
                }
            },
            {
                label: "Оплачено дней",
                field: "paidDays"
            },
            {
                label: "Сумма",
                render: item => {
                    const total = getCalculatedCardAmount(item);
                    return total > 0 ? `${total} ₽` : "-";
                }
            },
            {
                label: "Статус",
                field: "paymentStatus"
            }
        ],
        onCreate: openCreatePaymentCardModal,
        onEdit: editPaymentCard,
        onDelete: deletePaymentCard
    });
}

async function loadPaymentCardReferences() {
    try {
        const [registrationsData, roomsData, invoiceItemsData] = await Promise.all([
            apiRequest("/registrations"),
            apiRequest("/rooms"),
            apiRequest("/invoice-items")
        ]);

        paymentCardRegistrations = registrationsData;
        paymentCardRooms = roomsData;
        paymentCardInvoiceItems = invoiceItemsData;
    } catch (error) {
        alert(error.message);
    }
}

function paymentCardForm(card = {}) {
    const registrationId = card.registrationId || "";
    const paidDays = card.paidDays || "";

    const calculatedAmount = getCalculatedCardAmount(card);

    return `
        <div class="form-group">
            <label>Регистрация</label>
            <select name="registrationId" required>
                <option value="">Выберите регистрацию</option>
                ${paymentCardRegistrations.map(registration => `
                    <option value="${registration.id}" ${registration.id == registrationId ? "selected" : ""}>
                        Регистрация №${registration.id}
                    </option>
                `).join("")}
            </select>
        </div>

        <div class="form-group">
            <label>Оплачено дней</label>
            <input
                type="number"
                name="paidDays"
                min="1"
                value="${paidDays}"
                required
            >
        </div>

        <div class="form-group">
            <label>Итоговая сумма</label>
            <input
                type="text"
                value="${calculatedAmount > 0 ? calculatedAmount + ' ₽' : 'Будет рассчитана'}"
                readonly
                style="background: #f8f9fa; cursor: not-allowed; font-weight: bold;"
            >
            <div style="color: #64748b; font-size: 12px; margin-top: 4px;">
                💡 Рассчитывается автоматически: (Дни × Цена номера) + Сумма счетов
            </div>
        </div>

        <div class="form-group">
            <label>Статус оплаты</label>
            <select name="paymentStatus" required>
                <option value="Не оплачено" ${card.paymentStatus === "Не оплачено" ? "selected" : ""}>
                    Не оплачено
                </option>
                <option value="Оплачено" ${card.paymentStatus === "Оплачено" ? "selected" : ""}>
                    Оплачено
                </option>
                <option value="Частично оплачено" ${card.paymentStatus === "Частично оплачено" ? "selected" : ""}>
                    Частично оплачено
                </option>
            </select>
        </div>
    `;
}

async function openCreatePaymentCardModal() {
    try {
        await loadPaymentCardReferences();
    } catch (error) {
        alert(error.message);
        return;
    }

    openModal(`
        <div class="modal-header">
            <h2>Новая платёжная карта</h2>
        </div>
        <form id="payment-card-form">
            ${paymentCardForm()}
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

    document.getElementById("payment-card-form").addEventListener("submit", createPaymentCard);
}

// ✅ Теперь принимаем cardId, чтобы при редактировании учитывать счета именно этой карты
function getPaymentCardData(form, cardId = 0) {
    const data = new FormData(form);

    const registrationId = Number(data.get("registrationId"));
    const paidDays = Number(data.get("paidDays")) || 0;

    const tempCard = { id: cardId, registrationId, paidDays };
    const totalAmount = getCalculatedCardAmount(tempCard);

    return {
        registrationId: registrationId,
        paidDays: paidDays,
        totalAmount: totalAmount,
        paymentStatus: data.get("paymentStatus")
    };
}

async function createPaymentCard(event) {
    event.preventDefault();
    const form = event.target;

    if (!validateForm(form)) {
        return;
    }

    // При создании id = 0, поэтому счета не учитываются, берется только база (дни × цена)
    const card = getPaymentCardData(form, 0);

    if (card.totalAmount <= 0) {
        alert("Не удалось рассчитать сумму. Проверьте данные регистрации.");
        return;
    }

    try {
        await apiRequest("/payment-cards", {
            method: "POST",
            body: JSON.stringify(card)
        });

        closeModal();
        await loadPaymentCards();
    } catch (error) {
        alert(error.message);
    }
}

async function editPaymentCard(id) {
    const card = paymentCards.find(item => item.id === id);
    if (!card) return;

    try {
        await loadPaymentCardReferences();
    } catch (error) {
        alert(error.message);
        return;
    }

    openModal(`
        <div class="modal-header">
            <h2>Редактирование карты</h2>
        </div>
        <form id="payment-card-form">
            ${paymentCardForm(card)}
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

    document.getElementById("payment-card-form").addEventListener("submit", async event => {
        event.preventDefault();
        const form = event.target;

        if (!validateForm(form)) {
            return;
        }

        // ✅ При редактировании передаем реальный id карты, чтобы подтянуть её счета
        const updated = getPaymentCardData(form, id);

        if (updated.totalAmount <= 0) {
            alert("Не удалось рассчитать сумму. Проверьте данные регистрации.");
            return;
        }

        try {
            await apiRequest(`/payment-cards/${id}`, {
                method: "PUT",
                body: JSON.stringify(updated)
            });

            closeModal();
            await loadPaymentCards();
        } catch (error) {
            alert(error.message);
        }
    });
}

async function deletePaymentCard(id) {
    if (!confirm("Удалить платёжную карту?")) {
        return;
    }

    try {
        await apiRequest(`/payment-cards/${id}`, {
            method: "DELETE"
        });
        await loadPaymentCards();
    } catch (error) {
        alert(error.message);
    }
}
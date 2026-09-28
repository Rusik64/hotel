function loadDashboard() {

    const mainContent =
        document.getElementById("main-content");


    mainContent.innerHTML = `

        <div class="page">

            <div class="page-header">

                <div>

                    <h1>
                        Панель управления
                    </h1>

                    <p>
                        Система управления гостиницей
                    </p>

                </div>

            </div>


            <div class="dashboard-grid">

                <div class="stat-card">

                    <div class="stat-card-title">
                        Клиенты
                    </div>

                    <div
                        class="stat-card-value"
                        id="dashboard-clients"
                    >
                        —
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-card-title">
                        Номера
                    </div>

                    <div
                        class="stat-card-value"
                        id="dashboard-rooms"
                    >
                        —
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-card-title">
                        Регистрации
                    </div>

                    <div
                        class="stat-card-value"
                        id="dashboard-registrations"
                    >
                        —
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-card-title">
                        Услуги
                    </div>

                    <div
                        class="stat-card-value"
                        id="dashboard-services"
                    >
                        —
                    </div>

                </div>

            </div>

        </div>
    `;


    loadDashboardStats();
}


async function loadDashboardStats() {

    try {

        const [
            clientsData,
            roomsData,
            registrationsData,
            servicesData
        ] = await Promise.all([

            apiRequest("/clients"),

            apiRequest("/rooms"),

            apiRequest("/registrations"),

            apiRequest("/services")

        ]);


        if (clientsData) {
            document
                .getElementById("dashboard-clients")
                .textContent =
                clientsData.length;
        }


        if (roomsData) {
            document
                .getElementById("dashboard-rooms")
                .textContent =
                roomsData.length;
        }


        if (registrationsData) {
            document
                .getElementById("dashboard-registrations")
                .textContent =
                registrationsData.length;
        }


        if (servicesData) {
            document
                .getElementById("dashboard-services")
                .textContent =
                servicesData.length;
        }

    } catch (error) {

        console.error(
            "Ошибка загрузки статистики:",
            error
        );
    }
}


function loadPage(page) {

const user = getCurrentUser();
    const navLink = document.querySelector(`.nav-link[data-page="${page}"]`);

    if (navLink) {
        const allowedRoles = navLink.dataset.roles;
        if (allowedRoles) {
            const rolesList = allowedRoles.split(',');
            if (!user || !rolesList.includes(user.role)) {
                console.warn(`Доступ к странице "${page}" запрещен для роли ${user?.role}`);
                loadDashboard(); // Перенаправляем на главную
                return;
            }
        }
    }

    switch (page) {

        case "dashboard":
            loadDashboard();
            break;

        case "clients":
            loadClients();
            break;

        case "rooms":
            loadRooms();
            break;

        case "room-details":
            loadRoomDetails();
            break;

        case "registrations":
            loadRegistrations();
            break;

        case "payment-cards":
            loadPaymentCards();
            break;

        case "services":
            loadServices();
            break;

        case "service-provisions":
            loadServiceProvisions();
            break;

        case "invoice-items":
            loadInvoiceItems();
            break;

        case "employees":
            loadEmployees();
            break;

        default:
            loadDashboard();
    }
}


document
    .querySelectorAll(".nav-link")
    .forEach(link => {

        link.addEventListener("click", () => {

            document
                .querySelectorAll(".nav-link")
                .forEach(navLink => {

                    navLink.classList.remove("active");

                });


            link.classList.add("active");


            loadPage(
                link.dataset.page
            );

        });

    });


document.addEventListener(
    "DOMContentLoaded",
    () => {

        checkAuth();
        loadDashboard();

    }
);
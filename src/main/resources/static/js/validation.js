function clearFormErrors(form) {
    form.querySelectorAll(".field-error")
    .forEach(element => element.remove());
        form.querySelectorAll(".input-error")
        .forEach(element => {
            element.classList.remove("input-error");
        });
}

function showFieldError(field, message) {
    if (!field) {
        return;
    }
    field.classList.add("input-error");
    const error = document.createElement("div");
    error.className = "field-error";
    error.textContent = message;
    field.parentElement.appendChild(error);
}

function getField(form, name) {
    return form.elements[name];
}

function getFieldValue(form, name) {
    const field = getField(form, name);
    if (!field) {
        return "";
    }
    return field.value.trim();
}

function isEmpty(value) {
    return value === null || value === undefined || String(value).trim() === "";
}

function validateForm(form) {
    clearFormErrors(form);
    let valid = true;
    const errors = [];
    if (!form.checkValidity()) {
        const invalidFields = form.querySelectorAll(":invalid");
        invalidFields.forEach(field => {
            let message = field.validationMessage;
            if (field.validity.valueMissing) {
                message = "Поле обязательно для заполнения";
            }
            if (field.validity.typeMismatch) {
                message = "Введите корректное значение";
            }
            if (field.validity.patternMismatch) {
                message = field.title || "Значение имеет неправильный формат";
            }
            if (field.validity.rangeUnderflow) {
                message = `Значение не может быть меньше ${field.min}`;
            }
            if (field.validity.rangeOverflow) {
                message = `Значение не может быть больше ${field.max}`;
            }
            if (field.validity.tooLong) {
                message = `Максимальная длина: ${field.maxLength} символов`;
            }
            showFieldError(field, message);
            errors.push(message);
        });
        valid = false;
    }
    if (!validateClientFields(form)) {
        valid = false;
    }
    if (!validateDateFields(form)) {
        valid = false;
    }
    if (!validateNumericFields(form)) {
        valid = false;
    }
    return valid;
}

function validateClientFields(form) {
    let valid = true;
    const nameFields = [ "lastName", "firstName", "middleName" ];
    nameFields.forEach(name => {
        const field = getField(form, name);
        if (!field) {
            return;
        }
        const value = field.value.trim();
        if (value === "") {
            return;
        }
        const regex = /^[А-Яа-яЁёA-Za-zÀ-ÿ -]+$/u;
        if (!regex.test(value)) {
            showFieldError( field, "Поле может содержать только буквы, пробел и дефис" );
            valid = false;
        }
    });
    const passportSeries = getField(form, "passportSeries");
    if (passportSeries) {
        const value = passportSeries.value.trim();
        if ( value !== "" && !/^\d+$/.test(value) ) {
            showFieldError( passportSeries, "Серия паспорта должна содержать только цифры" );
            valid = false;
        }
    }
    const passportNumber = getField(form, "passportNumber");
    if (passportNumber) {
        const value = passportNumber.value.trim();
        if ( value !== "" && !/^\d+$/.test(value) ) {
            showFieldError( passportNumber, "Номер паспорта должен содержать только цифры" );
            valid = false;
        }
    }
    const phone = getField(form, "phone");
    if (phone) { const value = phone.value.trim();
    if ( value !== "" && !/^\+?[0-9()\-\s]+$/.test(value) ) {
        showFieldError( phone, "Введите корректный номер телефона" );
        valid = false;
    }
    }
    return valid;
}

function validateDateFields(form) {
    let valid = true;
    const birthDate = getField(form, "birthDate");
    if (birthDate && birthDate.value) {
        const date = new Date(birthDate.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (date >= today) {
            showFieldError( birthDate, "Дата рождения должна быть в прошлом" );
            valid = false;
        }
    }
    const arrivalDate = getField(form, "arrivalDate");
    const departureDate = getField(form, "departureDate");
    if ( arrivalDate && departureDate && arrivalDate.value && departureDate.value ) {
        const arrival = new Date(arrivalDate.value);
        const departure = new Date(departureDate.value);
        if (departure < arrival) {
            showFieldError( departureDate, "Дата выезда не может быть раньше даты заезда" );
            valid = false;
        }
    }
        const provisionDate = getField(form, "provisionDate");
        if (provisionDate && provisionDate.value) {
            const date = new Date(provisionDate.value);
            const today = new Date();
            today.setHours(23, 59, 59, 999); // разрешаем "сегодня"
            if (date > today) {
                showFieldError(provisionDate, "Дата оказания не может быть в будущем");
                valid = false;
            }
        }
    return valid;
}

function validateNumericFields(form) {
    let valid = true;
    const positiveFields = [ "price", "roomPrice", "dailyRoomPrice", "totalAmount", "amount" ];
    positiveFields.forEach(name => {
        const field = getField(form, name);
        if (!field || field.value === "") {
            return;
        }
        const value = Number(field.value);
        if (!Number.isFinite(value) || value <= 0) {
            showFieldError( field, "Значение должно быть больше 0" );
            valid = false;
        }
    });
    const nonNegativeFields = [ "availablePlaces", "actualResidents", "numberOfRooms", "floor" ];
    nonNegativeFields.forEach(name => {
        const field = getField(form, name);
        if (!field || field.value === "") {
            return;
        }
        const value = Number(field.value);
        if (!Number.isFinite(value) || value < 0) {
            showFieldError( field, "Значение не может быть отрицательным" );
            valid = false;
        }
    });
    const positiveIntegerFields = [ "paidDays", "quantity" ];
    positiveIntegerFields.forEach(name => {
        const field = getField(form, name);
        if (!field || field.value === "") {
            return;
        }
        const value = Number(field.value);
        if ( !Number.isInteger(value) || value < 1 ) {
            showFieldError( field, "Введите целое число не менее 1" );
            valid = false;
        }
    });
    return valid;
}

document.addEventListener( "input", event => {
    const field = event.target;
    if ( !field.classList.contains("input-error") ) {
        return;
    }
    field.classList.remove("input-error");
    const error = field.parentElement ?.querySelector(".field-error");
    if (error) {
        error.remove();
    }
}
);
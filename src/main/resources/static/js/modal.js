function openModal(content) {

    const modal = document.getElementById("modal");
    const modalBody = document.getElementById("modal-body");

    modalBody.innerHTML = content;

    modal.classList.add("show");
}


function closeModal() {

    const modal = document.getElementById("modal");

    modal.classList.remove("show");
}


document
    .getElementById("modal-close")
    .addEventListener("click", closeModal);


document
    .getElementById("modal")
    .addEventListener("click", event => {

        if (event.target.id === "modal") {
            closeModal();
        }

    });


document.addEventListener("keydown", event => {

    if (event.key === "Escape") {
        closeModal();
    }

});
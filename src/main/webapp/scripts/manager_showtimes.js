const addShowtimeDialog =
    document.getElementById("addShowtimeDialog");

const openAddShowtimeDialog =
    document.getElementById("openAddShowtimeDialog");

const closeAddShowtimeDialog =
    document.getElementById("closeAddShowtimeDialog");

const cancelAddShowtime =
    document.getElementById("cancelAddShowtime");

openAddShowtimeDialog.addEventListener("click", function () {
    addShowtimeDialog.showModal();
});

closeAddShowtimeDialog.addEventListener("click", function () {
    addShowtimeDialog.close();
});

cancelAddShowtime.addEventListener("click", function () {
    addShowtimeDialog.close();
});

const showtimePrice = document.getElementById("showtimePrice");
showtimePrice.addEventListener("input", function () {
    showtimePrice.value = showtimePrice.value.replace(/\D/g, "");
});

const scheduleTableWrapper = document.getElementById("scheduleTableWrapper");
const toggleDeleteMode = document.getElementById("toggleDeleteMode");
const deleteShowtimeDialog = document.getElementById("deleteShowtimeDialog");
const closeDeleteShowtimeDialog = document.getElementById("closeDeleteShowtimeDialog");
const cancelDeleteShowtime = document.getElementById("cancelDeleteShowtime");
const deleteShowtimeId = document.getElementById("deleteShowtimeId");
const deleteShowtimeMovie = document.getElementById("deleteShowtimeMovie");
const deleteShowtimeDetail = document.getElementById("deleteShowtimeDetail");

toggleDeleteMode.addEventListener("click", function () {
    const active = scheduleTableWrapper.classList.toggle("delete-mode");
    toggleDeleteMode.classList.toggle("active", active);
    toggleDeleteMode.textContent = active ? "Done" : "Delete Showtime";
});

document.querySelectorAll(".delete-showtime-button").forEach(function (button) {
    button.addEventListener("click", function () {
        deleteShowtimeId.value = button.dataset.showtimeId;
        deleteShowtimeMovie.textContent = button.dataset.movie;
        deleteShowtimeDetail.textContent =
            button.dataset.room + " · " + button.dataset.time;
        deleteShowtimeDialog.showModal();
    });
});

closeDeleteShowtimeDialog.addEventListener("click", function () {
    deleteShowtimeDialog.close();
});

cancelDeleteShowtime.addEventListener("click", function () {
    deleteShowtimeDialog.close();
});
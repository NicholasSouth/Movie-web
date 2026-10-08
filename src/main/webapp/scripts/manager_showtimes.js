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
    deleteMoviePanel.hidden = !active;
    if (!active) resetDeleteMovie();
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

const requestMovieDialog = document.getElementById("requestMovieDialog");
const openRequestMovieDialog = document.getElementById("openRequestMovieDialog");
const closeRequestMovieDialog = document.getElementById("closeRequestMovieDialog");
const cancelRequestMovie = document.getElementById("cancelRequestMovie");

openRequestMovieDialog.addEventListener("click", function () {
    requestMovieDialog.showModal();
});

closeRequestMovieDialog.addEventListener("click", function () {
    requestMovieDialog.close();
});

cancelRequestMovie.addEventListener("click", function () {
    requestMovieDialog.close();
});

const SUMMARY_READY = true;

const deleteMoviePanel = document.getElementById("deleteMoviePanel");
const deleteMovieSelect = document.getElementById("deleteMovieSelect");
const deleteMovieSummary = document.getElementById("deleteMovieSummary");
const deleteMovieSummaryText = document.getElementById("deleteMovieSummaryText");
const openDeleteMovieDialog = document.getElementById("openDeleteMovieDialog");
const deleteMovieShowtimesDialog = document.getElementById("deleteMovieShowtimesDialog");
const closeDeleteMovieDialog = document.getElementById("closeDeleteMovieDialog");
const cancelDeleteMovie = document.getElementById("cancelDeleteMovie");
const deleteMovieId = document.getElementById("deleteMovieId");
const deleteMovieName = document.getElementById("deleteMovieName");

let currentSummary = null;

function resetDeleteMovie() {
    deleteMovieSelect.value = "";
    deleteMovieSummary.hidden = true;
    currentSummary = null;
}

function plural(n, word) {
    return n + " " + word + (n === 1 ? "" : "s");
}

function renderSummary(movieName, data) {
    currentSummary = data;
    const total = data.deletable + data.blocked;

    if (total === 0) {
        deleteMovieSummaryText.textContent = "No upcoming showtimes in your theaters.";
        openDeleteMovieDialog.hidden = true;
        deleteMovieSummary.hidden = false;
        return;
    }

    deleteMovieSummaryText.textContent =
        data.deletable + " can be deleted · " + data.blocked + " kept (active bookings)";
    openDeleteMovieDialog.hidden = data.deletable === 0;
    openDeleteMovieDialog.textContent = "Delete " + plural(data.deletable, "showtime");
    deleteMovieSummary.hidden = false;
}

deleteMovieSelect.addEventListener("change", function () {
    const movieId = deleteMovieSelect.value;
    if (!movieId) {
        resetDeleteMovie();
        return;
    }
    const movieName = deleteMovieSelect.options[deleteMovieSelect.selectedIndex].text;

    fetch(deleteMoviePanel.dataset.summaryUrl + "?movieId=" + encodeURIComponent(movieId))
        .then(function (res) { return res.json(); })
        .then(function (data) { renderSummary(movieName, data); })
        .catch(function () {
            deleteMovieSummaryText.textContent = "Could not load showtimes. Try again.";
            openDeleteMovieDialog.hidden = true;
            deleteMovieSummary.hidden = false;
        });
});

openDeleteMovieDialog.addEventListener("click", function () {
    if (!currentSummary) return;
    deleteMovieId.value = deleteMovieSelect.value;
    deleteMovieName.textContent =
        deleteMovieSelect.options[deleteMovieSelect.selectedIndex].text;
    deleteMovieShowtimesDialog.showModal();
});

closeDeleteMovieDialog.addEventListener("click", function () {
    deleteMovieShowtimesDialog.close();
});
cancelDeleteMovie.addEventListener("click", function () {
    deleteMovieShowtimesDialog.close();
});
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
function toggleEditMode() {
    const section = document.getElementById("roomsSection");
    const editing = section.classList.toggle("editing");
    document.getElementById("editModeButton").classList.toggle("active", editing);
}

const addRoomDialog = document.getElementById("addRoomDialog");
function openAddRoomDialog() {
    addRoomDialog.querySelector("form").reset();
    addRoomDialog.showModal();
}

const editRoomDialog = document.getElementById("editRoomDialog");
function openEditRoomDialog(roomId, roomName, roomTypeId) {
    document.getElementById("editRoomId").value = roomId;
    document.getElementById("editRoomName").value = roomName;
    document.getElementById("editRoomType").value = roomTypeId;
    editRoomDialog.showModal();
}
document.querySelectorAll(".edit-btn").forEach(btn => {
    btn.addEventListener("click", () =>
        openEditRoomDialog(btn.dataset.roomId, btn.dataset.roomName, btn.dataset.roomTypeId));
});

const deactivateDialog = document.getElementById("deactivateDialog");
function openDeactivateDialog(roomId, roomName) {
    document.getElementById("deactivateRoomId").value = roomId;
    document.getElementById("deactivateRoomName").textContent = roomName;
    deactivateDialog.showModal();
}
document.querySelectorAll(".deactivate-btn").forEach(btn => {
    btn.addEventListener("click", () =>
        openDeactivateDialog(btn.dataset.roomId, btn.dataset.roomName));
});

const deleteDialog = document.getElementById("deleteDialog");
function openDeleteDialog(roomId, roomName) {
    document.getElementById("deleteRoomId").value = roomId;
    document.getElementById("deleteRoomName").textContent = roomName;
    deleteDialog.showModal();
}
document.querySelectorAll(".delete-btn").forEach(btn => {
    btn.addEventListener("click", () =>
        openDeleteDialog(btn.dataset.roomId, btn.dataset.roomName));
});
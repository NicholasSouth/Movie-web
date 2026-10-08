document.addEventListener("DOMContentLoaded", function () {
    const dialog = document.getElementById("requestDialog");
    if (!dialog) return;

    document.querySelectorAll("[data-open-request]").forEach(function (button) {
        button.addEventListener("click", function () {
            dialog.showModal();
        });
    });

    document.getElementById("closeRequestDialog").addEventListener("click", function () {
        dialog.close();
    });

    dialog.addEventListener("click", function (event) {
        if (event.target === dialog) dialog.close();
    });

    const leaveDialog = document.getElementById("leaveDialog");
    if (leaveDialog) {
        document.querySelectorAll("[data-leave-id]").forEach(function (button) {
            button.addEventListener("click", function () {
                document.getElementById("leaveTheaterId").value = button.dataset.leaveId;
                document.getElementById("leaveTheaterName").textContent = button.dataset.leaveName;
                document.getElementById("leaveReason").value = "";
                leaveDialog.showModal();
            });
        });

        document.getElementById("closeLeaveDialog").addEventListener("click", function () {
            leaveDialog.close();
        });

        leaveDialog.addEventListener("click", function (event) {
            if (event.target === leaveDialog) leaveDialog.close();
        });
    }
});
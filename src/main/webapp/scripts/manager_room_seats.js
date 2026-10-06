(function () {
    var EDIT_READY = true;      // /manager-seats/edit đã xong
    var TOGGLE_READY = true;

    var NAME_PATTERN = /^[A-Z][1-9][0-9]*$/;

    /* ---------- Hàm dùng chung ---------- */
    function setError(input, box, msg) {
        box.textContent = msg;
        box.hidden = false;
        input.classList.add('invalid');
    }
    function clearError(input, box) {
        box.hidden = true;
        input.classList.remove('invalid');
    }
    function seatElement(name) {
        return document.querySelector('.seat[data-seat="' + name + '"]');
    }
    function listNames(names) {
        var max = 8;
        return names.length <= max
            ? names.join(', ')
            : names.slice(0, max).join(', ') + ' and ' + (names.length - max) + ' more';
    }
    function closeOnBackdrop(d) {
        d.addEventListener('click', function (e) {
            if (e.target === d) d.close();
        });
    }
    function uppercaseNoSpaces(input, onChange) {
        input.addEventListener('input', function () {
            input.value = input.value.toUpperCase().replace(/\s+/g, '');
            onChange();
        });
    }

    /* ---------- Add seat dialog ---------- */
    var dialog = document.getElementById('addSeatDialog');
    var form   = document.getElementById('addSeatForm');
    var nameIn = document.getElementById('seatName');
    var errBox = document.getElementById('seatNameError');

    document.getElementById('openAddSeat').addEventListener('click', function () {
        form.reset();
        clearError(nameIn, errBox);
        dialog.showModal();
        nameIn.focus();
    });
    document.getElementById('cancelAddSeat').addEventListener('click', function () {
        dialog.close();
    });
    closeOnBackdrop(dialog);
    uppercaseNoSpaces(nameIn, function () { clearError(nameIn, errBox); });

    form.addEventListener('submit', function (e) {
        var name = nameIn.value.trim();
        if (!NAME_PATTERN.test(name)) {
            e.preventDefault();
            setError(nameIn, errBox, 'Invalid name. Use one uppercase letter followed by a number, e.g. A1.');
            return;
        }
        if (seatElement(name)) {
            e.preventDefault();
            setError(nameIn, errBox, 'Seat ' + name + ' already exists in this room.');
        }
    });

    /* ---------- Edit mode + selection ---------- */
    var editModeButton        = document.getElementById('editModeButton');
    var editToolbar           = document.getElementById('seatEditToolbar');
    var selectedSeatCount     = document.getElementById('selectedSeatCount');
    var clearSelectionBtn     = document.getElementById('clearSelectionBtn');
    var editSelectedBtn       = document.getElementById('editSelectedBtn');
    var activateSelectedBtn   = document.getElementById('activateSelectedBtn');
    var deactivateSelectedBtn = document.getElementById('deactivateSelectedBtn');
    var removeSelectedBtn     = document.getElementById('removeSelectedBtn');
    var seatsContainer        = document.querySelector('.seats');

    var selectedSeats = new Set();
    var editMode = false;

    function getSeatElements() {
        return document.querySelectorAll('.seat[data-seat]');
    }
    
    function updateSelectionCount() {
        var count = selectedSeats.size;
        selectedSeatCount.textContent = count + (count === 1 ? ' seat selected' : ' seats selected');
        var hasActive = false;
        var hasInactive = false;
        selectedSeats.forEach(function (name) {
            var el = seatElement(name);
            if (!el) return;
            if (el.dataset.active === 'true') hasActive = true;
            else hasInactive = true;
        });

        [editSelectedBtn, removeSelectedBtn, clearSelectionBtn].forEach(function (btn) {
            btn.disabled = (count === 0);
        });
        activateSelectedBtn.disabled   = (count === 0) || hasActive;
        deactivateSelectedBtn.disabled = (count === 0) || hasInactive;
    }

    function clearSelection() {
        selectedSeats.clear();
        getSeatElements().forEach(function (seat) {
            seat.classList.remove('selected');
        });
        updateSelectionCount();
    }

    function enterEditMode() {
        editMode = true;
        editModeButton.classList.add('active');
        editToolbar.classList.add('visible');
        if (seatsContainer) seatsContainer.classList.add('editing');
        clearSelection();
    }

    function exitEditMode() {
        editMode = false;
        editModeButton.classList.remove('active');
        editToolbar.classList.remove('visible');
        if (seatsContainer) seatsContainer.classList.remove('editing');
        clearSelection();
    }

    editModeButton.addEventListener('click', function () {
        if (editMode) exitEditMode();
        else enterEditMode();
    });

    getSeatElements().forEach(function (seat) {
        seat.addEventListener('click', function () {
            if (!editMode) return;
            var seatName = seat.dataset.seat;
            if (selectedSeats.has(seatName)) {
                selectedSeats.delete(seatName);
                seat.classList.remove('selected');
            } else {
                selectedSeats.add(seatName);
                seat.classList.add('selected');
            }
            updateSelectionCount();
        });
    });

    clearSelectionBtn.addEventListener('click', clearSelection);
    
    var editDialog    = document.getElementById('editSeatDialog');
    var editForm      = document.getElementById('editSeatForm');
    var editSummary   = document.getElementById('editSeatSummary');
    var editNameGroup = document.getElementById('editSeatNameGroup');
    var editNameIn    = document.getElementById('editSeatName');
    var editNameErr   = document.getElementById('editSeatNameError');
    var editTypeSel   = document.getElementById('editSeatType');
    var editSeatNames = document.getElementById('editSeatNames');

    document.getElementById('cancelEditSeat').addEventListener('click', function () {
        editDialog.close();
    });
    closeOnBackdrop(editDialog);
    uppercaseNoSpaces(editNameIn, function () { clearError(editNameIn, editNameErr); });

    editSelectedBtn.addEventListener('click', function () {
        if (selectedSeats.size === 0) return;

        var names = Array.from(selectedSeats);
        var single = names.length === 1;

        editForm.reset();
        clearError(editNameIn, editNameErr);
        editSeatNames.value = names.join(',');
        
        editNameGroup.hidden = !single;
        editNameIn.disabled = !single;
        if (single) {
            editSummary.textContent = 'Editing seat ' + names[0];
            editNameIn.value = names[0];
        } else {
            editSummary.textContent = 'Editing ' + names.length + ' seats: ' + listNames(names);
        }
        
        var typeIds = names.map(function (n) { return seatElement(n).dataset.seatTypeId; });
        var allSame = typeIds.every(function (t) { return t === typeIds[0]; });
        editTypeSel.value = allSame ? typeIds[0] : '';

        editDialog.showModal();
        if (single) editNameIn.focus();
    });

    editForm.addEventListener('submit', function (e) {
        var names = Array.from(selectedSeats);
        var newName = null;

        if (names.length === 1) {
            newName = editNameIn.value.trim();
            if (!NAME_PATTERN.test(newName)) {
                e.preventDefault();
                setError(editNameIn, editNameErr, 'Invalid name. Use one uppercase letter followed by a number, e.g. A1.');
                return;
            }
            if (newName !== names[0] && seatElement(newName)) {
                e.preventDefault();
                setError(editNameIn, editNameErr, 'Seat ' + newName + ' already exists in this room.');
                return;
            }
        }

        if (!EDIT_READY) {
            e.preventDefault();
            console.log('Edit seats:', names, { newName: newName, seatTypeId: editTypeSel.value });
            editDialog.close();
        }
    });
    
    var deactDialog  = document.getElementById('deactivateSeatDialog');
    var deactForm    = document.getElementById('deactivateSeatForm');
    var deactSummary = document.getElementById('deactivateSeatSummary');
    var deactNames   = document.getElementById('deactivateSeatNames');

    document.getElementById('cancelDeactivateSeat').addEventListener('click', function () {
        deactDialog.close();
    });
    closeOnBackdrop(deactDialog);

    deactivateSelectedBtn.addEventListener('click', function () {
        if (selectedSeats.size === 0) return;
        var names = Array.from(selectedSeats);
        deactNames.value = names.join(',');
        deactSummary.textContent = names.length === 1
            ? 'Deactivate seat ' + names[0] + '?'
            : 'Deactivate ' + names.length + ' seats: ' + listNames(names) + '?';
        deactDialog.showModal();
    });

    deactForm.addEventListener('submit', function (e) {
        if (!TOGGLE_READY) {
            e.preventDefault();
            console.log('Deactivate:', Array.from(selectedSeats));
            deactDialog.close();
        }
    });
    
    var removeDialog  = document.getElementById('removeSeatDialog');
    var removeSummary = document.getElementById('removeSeatSummary');
    var removeNames   = document.getElementById('removeSeatNames');

    document.getElementById('cancelRemoveSeat').addEventListener('click', function () {
        removeDialog.close();
    });
    closeOnBackdrop(removeDialog);

    removeSelectedBtn.addEventListener('click', function () {
        if (selectedSeats.size === 0) return;
        var names = Array.from(selectedSeats);
        removeNames.value = names.join(',');
        removeSummary.textContent = names.length === 1
            ? 'Remove seat ' + names[0] + '?'
            : 'Remove ' + names.length + ' seats: ' + listNames(names) + '?';
        removeDialog.showModal();
    });
    
    activateSelectedBtn.addEventListener('click', function () {
        if (selectedSeats.size === 0) return;

        var f = document.createElement('form');
        f.method = 'post';
        f.action = deactForm.action;  

        function addField(name, value) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = name;
            input.value = value;
            f.appendChild(input);
        }
        addField('roomId', deactForm.elements['roomId'].value);
        addField('seatNames', Array.from(selectedSeats).join(','));
        addField('active', 'true');

        document.body.appendChild(f);
        f.submit();
    });
})();
(function () {
    var seatsEl = document.querySelector('.seats');
    var confirmBtn = document.querySelector('.confirm-btn');
    var seatNameEl = document.querySelector('.summary-seat');
    var totalEl = document.querySelector('.summary-total');
    var bookingForm = document.querySelector('#bookingForm');
	
    function formatPrice(value) {
        return value.toLocaleString('vi-VN') + 'đ';
    }

    function updateSummary() {
        var selectedSeats = seatsEl.querySelectorAll('.seat.selected');
        var seatNames = [];
        var total = 0;
        selectedSeats.forEach(function (seat) {
            seatNames.push(seat.dataset.name);
            total += parseInt(seat.dataset.price, 10) || 0;
        });
        if (selectedSeats.length > 0) {
            seatNameEl.textContent = seatNames.join(', ');
            totalEl.textContent = 'Total: ' + formatPrice(total);
            confirmBtn.disabled = false;
        } 
		else {
            seatNameEl.textContent = '-';
            totalEl.textContent = 'Total: 0đ';
            confirmBtn.disabled = true;
        }
    }
    seatsEl.addEventListener('click', function (e) {
        var seat = e.target.closest('.seat');
        if (!seat ||
            seat.classList.contains('booked') ||
            seat.classList.contains('unavailable')) {
            return;
        }
        seat.classList.toggle('selected');
        updateSummary();
    });
    bookingForm.addEventListener('submit', function (e) {
        var selectedSeats = seatsEl.querySelectorAll('.seat.selected');
        if (selectedSeats.length === 0) {
            e.preventDefault();
            return;
        }

        // Remove seat IDs from a previous submission attempt.
        bookingForm.querySelectorAll('input[name="seat_id"]')
            .forEach(function (input) {
                input.remove();
            });

        // Submit every selected seat ID.
        selectedSeats.forEach(function (seat) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'seat_id';
            input.value = seat.dataset.seatId;
            bookingForm.appendChild(input);
        });
        confirmBtn.disabled = true;
        confirmBtn.textContent = 'Processing...';
    });
    updateSummary();
})();
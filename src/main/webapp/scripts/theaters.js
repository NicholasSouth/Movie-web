document.getElementById('find-near-btn').addEventListener('click', () => {
    const btn = document.getElementById('find-near-btn');
    const status = document.getElementById('find-near-status');

    if (!navigator.geolocation) {
        alert('The browser does not support geolocation.');
        return;
    }

    status.style.display = 'inline';
    btn.disabled = true;

    navigator.geolocation.getCurrentPosition(
        pos => {
            const lat = pos.coords.latitude;
            const lng = pos.coords.longitude;

            const params = new URLSearchParams(window.location.search);
            const currentSearch = params.get('search');

            let url = `theaters?userLat=${lat}&userLng=${lng}`;
            if (currentSearch) {
                url += `&search=${encodeURIComponent(currentSearch)}`;
            }
            window.location.href = url;
        },
        error => {
            status.style.display = 'none';
            btn.disabled = false;
            alert('Unable to retrieve your location. Please allow location access.');
        }
    );
});